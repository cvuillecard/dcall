# dcall

DCall is a draft of a terminal fully written in java based on DLT (Data Ledger Technology).

DCall is a project with the objective to construct a secure terminal control, able to protect user datas, host or distribute applications or datas on the public and/or private networks on which it is connected.



The architecture in micro-services, the p2p clustering approach and the cryptography by user fingerprint are natively integrated in the project to manage all the datas working for a user session in a local mode or a remote mode. In a great world, because it's in the road-map, the ultimate goal is to use this environment to generate on the fly a configured project with a ready to run application in java (maybe in python after ?) in which all resources are going to be crypted in a packaged archive which will be published on the cluster connected.



[ TODAY ]

​

The first step of the project was the GUI part and if there are few bugs, this looks actually quite stable, and we keep the control of the code for maintainability. We are speaking of a graphical terminal as a command line user interface. This terminal is a module of the project dcall and is a standalone application. This allows to keep control on what is done in the box and what is visible, runnable or not inside it.

​

Actually, when the terminal starts, tow solutions are possible in the terminal : 

​

1- Create a user

2 - Log in

​

Note : No passwords are stored, because all user datas are stored without the need of a trusted third party : the implementation of the user datas environment is based on the user identity, locally, and remotely soon.

​

Once the user is logged or created, the terminal starts in a local environment fully encrypted in a basic filesystem storage approach, to protect datas during the runtime.

An 'interpret_mode' allows to alternate between local interpreter and remote interpreter.

​

All user datas are crypted or decrypted lazily by the terminal on time, and all user commands run are versioned to save the current state, to be able to rollback in a previous state and trace all user operations in a versioned encrypted repository.

​

At user creation, the user fingerprint is used to generate the different resources fingerprints used for data encryption in a unique way (each data has its unique encoded name and its unique encryption secret key depending of the user fingerprint and its place on the filesystem). The project includes RSA encryption algorithm for certificate uses, and AES encryption for datas.

​

​

[ TOMORROW ]

​

The next step is about to start with the remote control, defining the default commands available on a peer node connected to the cluster associated to the terminal runtime.

A peer node is simply a standalone java application which is able to interpret remote commands, identify users or apps and do things on the node's cluster connected (identification, validation, data sharing, replication, identity searches and probably more..).



This step probably represents the biggest part of the implementation work because of the bottlenecks variety linked to replication strategies, resilience and reliability of network. The implementation must represent a fluent, smart and generic way to achieve all the necessary operations to distribute, coordinate, and synchronize datas  on a clustered  network.

​

​

[ AFTER ]

​

After this big rock, the last difficult part to give a sense to all this work, is to make the terminal reflective in a CLI object runtime context. With a reflective system to use classes on runtime as commands, we can create a simple little script language to compose what is a contract. I didn't test the java reflexion in a CLI way, but i did it in the past with php for fun and was surprised by the good performances of the php language. (So Quizz of what is a Dcall-contract ? only an api implementation or a little script that we deploy as an app or a block app ?)




Note : Configuration 'README' is coming, but don't hesitate to send me a mail at 'charles.vuillec@gmail.com' if you need help.

Maven configuration Notes : There are few useless dependencies because of the project in development (hibernate, jpa, neo4j, h2.. and every useless database dependencies with few spring dependencies and few exclusions of transitive dependencies) > will be removed, and a clean will be done later but they are really not a problem actually.
