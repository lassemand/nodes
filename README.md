### Task

We have a root node (only one) and several children nodes, each one with its own children as well. It's a tree-based structure. 
Something like:

         root

        /    \

       a      b

       |

       c

We need two HTTP APIs that will serve the two basic operations:
1) Get all descendant nodes of a given node (the given node can be anyone in the tree structure).
2) Change the parent node of a given node (the given node can be anyone in the tree structure).
They need to answer quickly, even with tons of nodes. Also,we can't afford to lose this information, so some sort of persistence is required.
Each node should have the following info:
1) node identification
2) who is the parent node
3) who is the root node
4) the height of the node. In the above example, height(root) = 0 and height(a) == 1.
We can only have docker and docker-compose on our machines, so your server needs to be run, using them.

### Comments

#### API

Due to the simple requirements a lightweight web server NanoHTTPD has been used. If for example some further requirements such 
as a authentication, would be added another web server would have to be used. This could for example be Jersey. 
Due to the few amounts of endpoints a regex template were used. NanoHTTPD provides a routing mechanism 
(https://github.com/NanoHttpd/nanohttpd/blob/350ed420bc91cfa8b6f57478691777fd6499363b/nanolets/src/main/java/fi/iki/elonen/router/RouterNanoHTTPD.java), 
however when only two endpoints have to be provided this gives a better overview. 

As a response for the GET endpoint a JSON structure were provided. The PUT method contains no response but the status code
The two endpoints are as follows:  

Get all descendant nodes: /api/v1/node/([0-9]+)/descendant

Update a parent node: /api/v1/node/([0-9]+)/parent/([0-9]+)

#### Persistance

A simple file is used as persistance. The entire node structure is then serialized into some binary format. 
The reason for this approach is two reasons: 

1. It is the fastest approach. Once an update occurs, the previous file can be overridden without even looking at the content.
Obviously the size of the serialization is much smaller than any other general purpose db. 
The serialization is as follows: Root node id / size of nodes array / every nodes parent and own id
2. No external db is required (Other dbs gives this same advantage such as SQLite, however it is much slower than this approach)

ZigZag encoding were used for the integer part. Read more about that here: https://gist.github.com/mfuerstenau/ba870a29e16536fdbaba

#### Docker

The target folder is expected to be where the docker image is built from. This is because the jar file can be copied into the 
image from there, which gives the advantage that the image used does not have to include building tools, providing us with
a much smaller image. Therefore before building images remember to do mvn install

It is expected that the user of the image provides the container with a volume into the /backup folder, in order to provide persistance. The port 
in which the web server uses has to be exposed. An example of running this webserver could be: 

docker run -it -v backup:/backup -p 8083:8080 lmapwns/nodes

#### Algorithmic

I define the parent chain as all the nodes a given node is child of.

A n x n matrix is built up, where each entry contains a bit indicating whether the corresponding node is a child of the source node. 
The source node is the rows. An observation is that a row can be cleared by using a simple xor with its child. By representing the
 rows in bigintegers, 64 such bits can be set in each clockcycle on a modern cpu. 
 
Using this observations, it is trivial changing a parent node for some given node. Simply xor all nodes in the parent chain until some common
ancestor, with the new parent is found, which resets all the children of the node. Using the same approach for the new
parent until the common ancestor sets all the new children for all nodes. 
A common ancestor can easily be distinguished because it has it bit set for both the source and the target.

It is evident that using this approach getting child nodes is constant where as the update
operation has to update all the nodes until some common ancestor. However as the height is calculated for all the 
child nodes once a get operation is performed, the total cost is \BigTheta(|childNodes|) by using dynamic programming
to update the height for all children. The common ancestor is found by getting the height up until the root.

Even though the current requirements only support updating of data, it is trivial adding a new node.
If a new node is added it only points to one parent, and no other nodes is children of the recently added node.
Because the current model is made of big integers, the previous rows does not even have to be updated.

#### Benchmarks

The slowest updates is performed on a chain where each node is having on unique child. Using such structure with size 20000 the cpu time one 
a modern non server based cpu is:

For getting all the children of the root node: 0.488 seconds, where 0.094 seconds is done calculating the height, and 0.156
seconds is used for serialization. The rest is communication overhead. A faster approach would be to serialize into a binary,
however it would make it not human readable.

Using the same chain updating the node number 5000 in the chain to the root node is taking 0.507 seconds including backing up and
communication overhead

#### Known issues

None
