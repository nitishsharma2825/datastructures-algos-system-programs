public class segtree {
    // tree starts from 1 index to use 2*n and 2*n+1 property
    public static class SegmentTree {
        private int[] inner;
        private int base_length;
        private final int GARBAGE_NODE_VALUE = 0;
        public SegmentTree(int[] base) {
            // find the length of inner array
            int len = 1;
            while (len < base.length) {
                len*=2;
            }
            this.inner = new int[2 * len];
            this.base_length = len;

            // fill leaves of segment tree [base template is for sum query]
            for (int i = 0; i < base.length; i++) {
                this.inner[i + len] = base[i];
            }

            // pad the extended leaves due to power of 2
            for (int i = base.length; i < len; i++) {
                this.inner[i + len] = this.GARBAGE_NODE_VALUE;
            }

            // fill the inner nodes of tree
            for (int i = len - 1; i >= 1; i--) {
                this.inner[i] = this.inner[2*i] + this.inner[(2*i)+1];
            }
        }

        // range query method
        // node = 1, node_low = 0, now_high = len-1 [inclusive] for head i.e node 1 at beginning
        // here indexes are 0-based on base array
        public int query(int node, int node_low_index, int node_high_index, int query_low_index, int query_high_index) {
            if (query_low_index > query_high_index) {
                return 0;
            }

            if (node_low_index == query_low_index && node_high_index == query_high_index) {
                return this.inner[node];
            }

            int node_mid_index = node_low_index + (node_high_index - node_low_index)/2;
            int left_ans = query(2*node, node_low_index, node_mid_index, query_low_index, Math.min(query_high_index, node_mid_index));
            int right_ans = query((2*node) + 1, node_mid_index+1, node_high_index, Math.max(node_mid_index + 1, query_low_index), query_high_index);
            return left_ans + right_ans;
        }

        // point update method
        public void pointUpdate(int node, int node_low_index, int node_high_index, int position, int value) {
            if (node_low_index == node_high_index) {
                this.inner[node] = value;
            } else {
                int node_mid_index = node_low_index + (node_high_index - node_low_index)/2;
                if (position <= node_mid_index) {
                    pointUpdate(2*node, node_low_index, node_mid_index, position, value);
                } else {
                    pointUpdate((2*node)+1, node_mid_index + 1, node_high_index, position, value);
                }

                // update the inner node after leaf update
                this.inner[node] = this.inner[2*node] + this.inner[(2*node) + 1];
            }
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for(int i = 1; i < this.inner.length; i++){
                sb.append("Index: ").append(i).append("; Value: ").append(this.inner[i]).append("\n");
            }
            return sb.toString();
        }
    }
    public static void main(String[] args) {
        int[] example = {1, 2, 3, 4, 5};
        SegmentTree tree = new SegmentTree(example);

        System.out.println(tree.query(1, 0, tree.base_length - 1, 0, 3));
        tree.pointUpdate(1, 0, tree.base_length - 1, 2, 10);
        System.out.println(tree.query(1, 0, tree.base_length - 1, 0, 3));

        System.out.println(tree.toString());
    }
}
