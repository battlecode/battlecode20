package battlecode.common;

public class BlockchainEntry implements Comparable<BlockchainEntry> {
    public int cost;
    public int[] message;
    public String serializedMessage;

    public BlockchainEntry(int cost, int[] message) {
        this.cost = cost;
        this.message = message;
        String[] stringMessageArray = new String[message.length];
        for (int i = 0; i < message.length; i++) {
            stringMessageArray[i] = Integer.toString(message[i]);
        }
        this.serializedMessage = String.join("_", stringMessageArray);
    }

    // getters

    @Override
    public int compareTo(BlockchainEntry other) {
        return this.cost - other.cost;
    }
}
