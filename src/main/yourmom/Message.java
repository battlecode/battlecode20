package yourmom;

/**
 * Message interface for sending broadcasts.
 * WARNING: Must ensure that only last 24 bits of body are used
 */
public class Message {
        public boolean isValid = false;
        public boolean isUnwritten = false;
        public int body;
        
        public Message(int body, boolean isValid, boolean isUnwritten) {
                this.body = body;
                this.isValid = isValid;
                this.isUnwritten = isUnwritten;
        }
        
        public Message(boolean isValid, boolean isUnwritten) {
                this(0, isValid, isUnwritten);
        }
        
}
