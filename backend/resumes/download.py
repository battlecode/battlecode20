import os, csv
from google.oauth2 import service_account
from google.cloud import storage
FILE_PATH = os.path.dirname(__file__)
os.sys.path.append(os.path.join(FILE_PATH, '..'))
from dev_settings_real import GOOGLE_APPLICATION_CREDENTIALS

# constants, please configure
GCLOUD_BUCKET_RESUMES = 'bc20-resumes'
BUCKET_MIN = 1600
BUCKET_MAX = 5000
USERS_ALL_PATH = os.path.join(FILE_PATH, 'users_all.csv')
USERS_TEAMS_PATH = os.path.join(FILE_PATH, 'users_teams.csv')

# load up the sql query results, as list of dictionaries
users_all = []
users_all_header = []
with open(USERS_ALL_PATH, 'r') as csvfile:
    reader = csv.reader(csvfile)
    users_all_header = next(reader)
    for row in reader:
        row_dict = dict()
        for i in range (0, len(row)):
            key = users_all_header[i]
            row_dict[key] = row[i]
        users_all.append(row_dict)
# print(users_all[:3])

users_teams = []
users_teams_header = []
with open(USERS_TEAMS_PATH, 'r') as csvfile:
    reader = csv.reader(csvfile)
    users_teams_header = next(reader)
    for row in reader:
        row_dict = dict()
        for i in range (0, len(row)):
            key = users_teams_header[i]
            if key in ["high_school", "international", "student"]:
                # we want booleans for these
                row_dict[key] = bool(row[i])
            else:
                row_dict[key] = row[i]
        users_teams.append(row_dict)
# print(users_teams[:3])

# initialize google bucket things
with open('gcloud-key.json', 'w') as outfile:
    outfile.write(GOOGLE_APPLICATION_CREDENTIALS)
    outfile.close()
os.environ["GOOGLE_APPLICATION_CREDENTIALS"] = str(os.path.join(os.path.dirname(__file__), '../../gcloud-key.json'))
client = storage.client.Client()
os.remove('gcloud-key.json') # important!!!
bucket = client.get_bucket(GCLOUD_BUCKET_RESUMES)

# initialize file paths for downloads
def safe_makedirs(directory):
    if not os.path.exists(directory):
        os.makedirs(directory)
files_dir = os.path.join(FILE_PATH, 'files')
safe_makedirs(files_dir)
hs_us_dir = os.path.join(files_dir, 'hs-us')
safe_makedirs(hs_us_dir)
hs_intl_dir = os.path.join(files_dir, 'hs-intl')
safe_makedirs(hs_intl_dir)
col_us_dir = os.path.join(files_dir, 'col-us')
safe_makedirs(col_us_dir)
col_intl_dir = os.path.join(files_dir, 'col-intl')
safe_makedirs(col_intl_dir)
others_dir = os.path.join(files_dir, 'others')
safe_makedirs(others_dir)

# download helper!
def download(user_id, file_name, bucket, files_dir):
    try:
        blob = bucket.get_blob(os.path.join(str(user_id), 'resume.pdf'))
        with open(os.path.join(files_dir, file_name), 'wb+') as file_obj:
            blob.download_to_file(file_obj)
            file_obj.close()
    except PermissionError:
        print("Could not obtain permissions to save; try running as sudo")
    except Exception as e:
        print("Could not retrieve source file from bucket, user id", user_id)
        print("Exception:", e)

# actually download resumes
# download first from users_teams
# figure out which folder to use programatically
# file name: "0ELO-FirstLast" (elo left padded, min 0)
# for user in users_teams:


# if a user is in users_all but not users_teams, dl them to "other"
user_id = 1706
file_name = 'subfolder/' + str(user_id)+'.pdf'
download(user_id, file_name, bucket, files_dir)
