Kenyon Wensing
Data Comm.
Homework 3 README file

My design has 3 major steps that it takes to get to the output:
1. Build the graph.
2. Build a tree of shortest paths.
3. Traverse the tree to print out the nodes along each path.

Building the graph:
Each graph node stores its name and a map containing its neighbors and the cost to each
The graph is build first by initializing all the nodes (vertices) and then reading in edge data until the graph is fully realized. The time complexity for the step is O(V+E) because the program must run initalization code for all nodes and edges.

Building the tree:
Next I contruct the tree by comparing the shortest path-to-source of undiscovered nodes for each discovered node in the tree and then storing that node in a tree node for the shortest path tree.  This is repeated until all nodes in the graph have been added to the tree. The time complexity for this step is O(V^2), but with use of a list of visited nodes I prevent any cycles from forming and I only check nodes where edges actually exist when looking for the next closest node, so the program only needs to compare a handful of possiblilities at any one time instead of wasting time searching for nodes it doesnt have access to.

Traversing the tree:
Finally I print the shortest path to each node by traversing the tree and printing out each node in the path as it is reached.  I made another method that can check a branch of the tree for a given node to allow me to only got down the correct branches with my printing method which returns the path-to-source cost to the tree-printing method to be printed before proceding to the next node.  The time complexity for this step is worst-case (V^2) because it must do a worst-case O(V) tree traversal for each node to be printed.