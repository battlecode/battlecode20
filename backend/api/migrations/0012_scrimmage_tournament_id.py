# Generated by Django 2.2.8 on 2020-01-12 19:36

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('api', '0011_auto_20200110_0821'),
    ]

    operations = [
        migrations.AddField(
            model_name='scrimmage',
            name='tournament_id',
            field=models.IntegerField(null=True),
        ),
    ]
