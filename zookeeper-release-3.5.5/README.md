# Apache ZooKeeper
启动函数详见：zkServer.cmd、zkCli.cmd
服务端：ZooKeeperServerMain
客户端：ZooKeeperMain，jline包有问题，控制台。。。
日志：将conf目录下的log4j日志放到only-parent/zookeeper-release-3.5.5/zookeeper-server/target/classes目录下

集群配置详见conf目录：zoo1.cfg、zoo2.cfg、zoo3.cfg

集群启动：

Quorum1：
    Main class：org.apache.zookeeper.server.quorum.QuorumPeerMain
    Program arguments：D:\only\only-parent\zookeeper-release-3.5.5\conf\zoo1.cfg(以实际路径为准)
Quorum2：
    Main class：org.apache.zookeeper.server.quorum.QuorumPeerMain
    Program arguments：D:\only\only-parent\zookeeper-release-3.5.5\conf\zoo2.cfg(以实际路径为准)
    VM options：-Dzookeeper.serverCnxnFactory=org.apache.zookeeper.server.NettyServerCnxnFactory(以Netty启动)
Quorum3：
    Main class：org.apache.zookeeper.server.quorum.QuorumPeerMain
    Program arguments：D:\only\only-parent\zookeeper-release-3.5.5\conf\zoo3.cfg(以实际路径为准)



![alt text](https://zookeeper.apache.org/images/zookeeper_small.gif "ZooKeeper")

For the latest information about Apache ZooKeeper, please visit our website at:

   http://zookeeper.apache.org/

and our wiki, at:

   https://cwiki.apache.org/confluence/display/ZOOKEEPER

## Packaging/release artifacts

Either downloaded from https://zookeeper.apache.org/releases.html or
found in zookeeper-assembly/target directory after building the project with maven.

    apache-zookeeper-[version].tar.gz

        Contains all the source files which can be built by running:
        mvn clean install

        To generate an aggregated apidocs for zookeeper-server and zookeeper-jute:
        mvn javadoc:aggregate
        (generated files will be at target/site/apidocs)

    apache-zookeeper-[version]-bin.tar.gz

        Contains all the jar files required to run ZooKeeper
        Full documentation can also be found in the docs folder

As of version 3.5.5, the parent, zookeeper and zookeeper-jute artifacts
are deployed to the central repository after the release
is voted on and approved by the Apache ZooKeeper PMC:

  https://repo1.maven.org/maven2/org/apache/zookeeper/zookeeper/

## Java 8

If you are going to compile with Java 1.8, you should use a
recent release at u211 or above. 

## Contributing
We always welcome new contributors to the project! See [How to Contribute](https://cwiki.apache.org/confluence/display/ZOOKEEPER/HowToContribute) for details on how to submit patch through pull request and our contribution workflow.


