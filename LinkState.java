import java.io.File;
import java.util.*;

public class LinkState {

    //variables used in running of the program
    private static final float DEFAULT_COST = Float.MAX_VALUE/2;

    private static Scanner s = new Scanner(System.in);
    private static Scanner sc;
    private List<LinkNode> vertices = new ArrayList<>();
    private static int sourcePoint;
    private static boolean validInput;
    private static int nodeCounter = 0;

    /**
     * Dummy main class, calls runLinkStateAlgorithm to run program
     * @param args- unused args array
     */
    public static void main(String[] args) {
        LinkState ls = new LinkState();
        ls.runLinkStateAlgorithm(args);

    }

    /**
     * This method is called by main. runLinkStateAlgorithm creates a graph, fins shortest path to each node from the
     * source point, and prints out the results.
     */
    private void runLinkStateAlgorithm(String[] args) {
        ShortestPathTree pathTree;
        sourcePoint = 0;
        validInput = false;

        readInFile(args);
        setUpGraph();

        if(sourcePoint > vertices.size()){
            validInput = false;
            System.out.println("Source Point Invalid: Try Again");
        }

        if(validInput){
            pathTree = new ShortestPathTree(0f, vertices.get(sourcePoint));
            pathTree.makeTree();
            pathTree.printTree();
        }
    }

    /**
     * Reads in input file and creates links in the graph
     */
    private void setUpGraph(){
        try {
            for (int i = 0, nodeCount = sc.nextInt(); i < nodeCount; i++)
                vertices.add(new LinkNode());

            int primaryNode;
            int secondaryNode;
            float cost;
            while (sc.hasNext() && !sc.hasNext("-1")) {
                primaryNode = sc.nextInt();
                secondaryNode = sc.nextInt();
                cost = sc.nextFloat();
                vertices.get(primaryNode).setLink(vertices.get(secondaryNode), cost);
                vertices.get(secondaryNode).setLink(vertices.get(primaryNode), cost);
            }
            validInput = true;
        }
        catch (Exception e){ validInput = false; }
    }

    /**
     * Reads in file and sourcePoint, catches exceptions in inputs
     */
    private static void readInFile(String[] args){
        try {
            if(args.length == 0) {
                System.out.println("Input your file path:");
                sc = new Scanner(new File(s.next()));
                System.out.println("Input source point:");
                sourcePoint = s.nextInt();
                validInput = true;
            }
            else{
                sc = new Scanner(new File(args[0]));
                sourcePoint = Integer.parseInt(args[1]);
            }
        } catch (Exception e) {
            System.out.println("Bad Input: Using defaults");
            try {
                sc = new Scanner(new File(
                        "C:\\Users\\kensi\\IdeaProjects\\DijkstrasAlgorithmProject2\\src\\GraphInput"));
                sourcePoint = 0;
                validInput = true;
            } catch (Exception ex) {
                System.out.println("Bad Path in defaults: Please input data manually or try again.");
                System.out.println("Enter # of nodes followed by any links, -1 to exit");
                sc = s;
            }
        }
    }

    /**
     * LinkNode Class where each node is a vertex of a graph.
     * The class stores all links for a single node and the cost of each link.
     */
    class LinkNode {
        private int nodeName;
        private Map<LinkNode, Float> neighbors;

        /**
         * Default constructor for a LinkNode which gives it a unique name and initiates is neighbor map
         */
        LinkNode(){
            nodeName = nodeCounter++;
            neighbors = new HashMap<>();
        }

        void setLink(LinkNode neighbor, Float cost){
            neighbors.put(neighbor, cost);
        }

        int getNodeName() {
            return nodeName;
        }

        List<LinkNode> getNeighbors(){
            return new ArrayList<>(neighbors.keySet());
        }

        float costToNeighbor(LinkNode neighbor) {
            return neighbors.get(neighbor);
        }
    }

    /**
     * ShortestPathTree class where each ShortestPathTree is a node of the full tree
     * The class stores all children of each node and methods for calculating and printing the paths.
     */
    class ShortestPathTree {

        private List<ShortestPathTree> children = new ArrayList<>();
        private float costToSource;
        private LinkNode linkNode;
        private List<Boolean> visited;

        /**
         * Constructor for ShortestPathTree that takes a float costToSource and LinkNode that the Shortestpathtree
         * node represents
         * @param costToSource- cost from source to the node
         * @param linkNode- node represented by this ShortestPathTree object
         */
        ShortestPathTree(Float costToSource, LinkNode linkNode){
            this.costToSource = costToSource;
            this.linkNode = linkNode;
            visited = new  ArrayList<>(Collections.nCopies(vertices.size(), false));
        }

        /**
         * This method creates a tree of ShortestPathTree nodes representing the shortest path to the source node from
         * each vertex
         */
        void makeTree() {
            ShortestPathTree nextClosestNodeParent;
            visited.set(sourcePoint, true);

            while(visited.contains(false)) {
                SPPair nextBestPair = findClosetNode(visited);

                nextClosestNodeParent = findParentNode(nextBestPair.getPrecedingNode());

                Objects.requireNonNull(nextClosestNodeParent).children.add(
                        new ShortestPathTree(nextBestPair.getCostToSource(),
                                vertices.get(nextBestPair.getCurrentNode())));
                visited.set(nextBestPair.getCurrentNode(), true);
            }
        }

        /**
         * This method is used by makeTree to convert an int precedingNode into the ShortestPathTree object it
         * represents.
         * @param precedingNode- int representing a ShortestPathTree node
         * @return - ShortestPathTree node represented by precedingNode
         */
        private ShortestPathTree findParentNode(int precedingNode) {
            ShortestPathTree possibleParent;
            if(this.linkNode.getNodeName() == precedingNode)
                return this;
            for(ShortestPathTree child: children) {
                possibleParent = child.findParentNode(precedingNode);
                if (possibleParent != null)
                    return possibleParent;
            }
            return null;
        }

        /**
         * This method is used by makeTree to find the name of the next node to add to the tree, the cost to the source
         * from that node, and the name of the preceding node.
         * @param visited - list of whether each node has been visited already
         * @return - SSPair containing the cost to source, preceding node, and name of node
         */
        private SPPair findClosetNode(List<Boolean> visited) {
            List<SPPair> pairs = new ArrayList<>();
            int nextClosestNodeIndex = -1;
            float minCost = DEFAULT_COST;
            float costFromNextToSource;
            float costToNeighbor;

            //find next non-visited vertex closest to source
            for(LinkNode vertex: linkNode.getNeighbors())
                if (!visited.get(vertices.indexOf(vertex))) {
                    costToNeighbor = linkNode.costToNeighbor(vertex);
                    costFromNextToSource = costToNeighbor + costToSource;
                    if (costFromNextToSource <= minCost) {
                        nextClosestNodeIndex = vertex.getNodeName();
                        minCost = costFromNextToSource;
                    }
                }

            pairs.add(new SPPair(nextClosestNodeIndex, linkNode.getNodeName(), minCost));

            //run recursively for any child nodes in tree and add best pair to the list
            if(!children.isEmpty())
                for(ShortestPathTree child: children)
                    pairs.add(child.findClosetNode(visited));

            //find node pair closest to source
            SPPair closestNodePair = pairs.get(0);
            for(SPPair pair: pairs)
                if(closestNodePair.getCostToSource() > pair.getCostToSource())
                    closestNodePair = pair;

            return closestNodePair;
        }

        /**
         * This method prints out the non-source nodes in ascending order along with their shortest path and cost to source.
         */
        void printTree() {
            float cost;
            for(LinkNode vertex: vertices){
                if(vertex.getNodeName() != sourcePoint){
                    System.out.print("shortest path to node " + vertex.getNodeName() + " is ");
                    cost = this.printPath(vertex.getNodeName());
                    System.out.println(" with cost " + cost);
                }
            }
        }

        /**
         * This method is used by printTree to print the path to a given node.
         * @param nodeName - node to print to
         * @return - costToSource for that node
         */
        private float printPath(int nodeName){
            if(this.linkNode.getNodeName() == nodeName) {
                System.out.print("->");
                System.out.print(this.linkNode.getNodeName());
                return costToSource;
            }
            for(ShortestPathTree child: children) {
                if(child.pathContains(nodeName)) {
                    if(this.linkNode.getNodeName() != sourcePoint)
                        System.out.print("->");
                    System.out.print(this.linkNode.getNodeName());
                    return child.printPath(nodeName);
                }
            }
            return 0f;
        }

        /**
         * This method is used by printPath to avoid printing nodes from the wrong branch of the tree.
         * @param nodeName - node to search for
         * @return - if the node is in that branch of the tree
         */
        private boolean pathContains(int nodeName) {
            if(this.linkNode.getNodeName() == nodeName)
                return true;
            boolean isInPath = false;
            for(ShortestPathTree child: children)
                if(child.pathContains(nodeName))
                    isInPath = true;
            return isInPath;
        }
    }

    /**
     * This class is a data structure used by ShortestPathTree to return multiple data elements in one object.
     */
    class SPPair {
        private int currentNode;
        private int precedingNode;
        private float costToSource;

        /**
         * Constructor for an SourceCost PrecedingNode Pair with an extra parameter containing the node it applies to
         * @param currentNode- Node SP pair applies to
         * @param precedingNode- Node preceding this node
         * @param costToSource- cost of path the source node
         */
        SPPair(int currentNode, int precedingNode, float costToSource) {
            this.currentNode = currentNode;
            this.precedingNode = precedingNode;
            this.costToSource = costToSource;
        }

        int getPrecedingNode() {
            return precedingNode;
        }

        float getCostToSource() {
            return costToSource;
        }

        int getCurrentNode() {
            return currentNode;
        }

    }
}