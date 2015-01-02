Growing Object Oriented Software, Guided by Tests (GOOS) is a well known and respected book focusing on the design feedback provided by Test Driving software development. As discussed at [insert link], this is my first attempt at the comprehensive worked example - the Auction Sniper. Whilst the final solution is of interest, perhaps of even more value is reviewing the commit history to see how I journey from an initial failing end to end test to a working solution which communicates to remote servers, presents a Swing UI and does so with low coupling / high cohension.  


# Installation

This, my solution to the Auction Sniper worked example primarily consists of a Java source tree, with dependency and build implemnted using idiomatic Maven. The test and integration-test phases cover the unit testing, and end to end testing requirements of the worked example.  

The Auction Sniper solution requires an XMPP server, so I've bundled a Vines instance in the poorly named vagrant submodule, as a Docker container to simplify deployment, and enable Continous Integration on CircleCi


## Prerequisites

* Java 7
* Maven
* Docker

## Clone the repo and submodules
    git clone git@github.com:paulspencerwilliams/auctionsniperjava.git
    git submodule update --init

## Build and run Vines XMPP server using Docker
Build the Vines XMPP server Docker image by

    docker build -t 'paulswilliams/vines' vagrant/
    
Once complete, run the Docker image

    docker run -p 5222:5222 -t 'paulswilliams/vines'

If deploying on a Mac with Boot2Docker, create an ssh tunnel to enable native to Docker VM communication. 

    boot2docker ssh -L 5222:localhost:5222

## Obtain certificate from Vines into Java keystore

To enable the Smack XMPP client to communicate with Vines, it is essential to download, and register the Vines TLS certificate with the Auction Sniper code:

    docker cp `docker ps | grep paulswilliams/vines | awk '{print $1;}'`:/localhost/conf/certs/localhost.crt certificates/

Import certificate into expected local keystore

    keytool -import -alias localhost -file certificates/localhost.crt  -keystore certificates/akeystore.jks 

And select a password

## And run the tests!!

    mvn integration-test

# Cleanup

To remove the worked example from your machine, stop and delete the Vines Docker container, and then delete the git clone. 

## Stop and Delete the Vines Docker container 

Stop the Docker container

    docker stop `docker ps | grep paulswilliams/vines | awk '{print $1;}'`

Delete the Docker container

    docker rmi 'paulswilliams/vines' 

# Challenges

## Smack Api changes

## Vines differing handling of multiple connections

## Impedance mismatch between Mockito and JMock
### Mockito not strict mock...

# Key lessons
 
## anonymous classes are okay in the short term. Keep them inline until you understand sufficiently to refactor them out - remember the inline before refactor technique?

## Learning is key, not the code. That's why I deleted it all!!

## Disable debugging - run tests that fail to show failure is easy to follow

## Keeping the tests real simple and lean

## Defining variables in unit tests final!

## Loose verifications

    verify(auction, times(1)).bid(price + increment);
    verify(sniperListener, atLeastOnce()).sniperBidding();
    
###
However, JMock has default strict mocks, so they're not that loose. Yes, allowances / when / then helps, but enough?

Although didn't fail when introducing show winning functionality

##
Refactoring not done in a way to allow too many small commits.

## The whole JMock allowances / status functionality

## The value of a supporting to do list to prevent flow breakage

## Avoid primative obsession

Including use of generics on collections where type is a duplication smell.

## Actually right the unit tests!

Remember that AuctionSniper bug when you were being lazy?


## TDD a feature with just one field, and then expand

## Extract a logging class, and test it's invoked!

## Struggling to remember a use case for states

but I'd like to learn more, seem a useful tool to keep in the bag
