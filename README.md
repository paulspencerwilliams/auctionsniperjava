# Original intention

# This Java attempt

# Installation

## Prerequisites
    You'll need Java 7, Maven and Docker

## Clone the repo and submodules
    git clone git@github.com:paulspencerwilliams/auctionsniperjava.git
    git submodule update

## Build and run Vines XMPP server using Docker
Build the docker image by

    docker build -t 'paulswilliams/vines' vagrant/
    
and wait, and wait, and wait

Once complete, run the docker image

    docker run -p 5222:5222 -t 'paulswilliams/vines'

## Obtain certificate from Vines into Java keystore
Get container id using 

    docker ps | grep paulswilliams

Retreive certificate from Docker

    docker cp {container id}:/localhost/conf/certs/localhost.crt certificates/ 

Import certificate

    keytool -import -alias localhost -file certificates/localhost.crt  -keystore certificates/akeystore.jks 
And select a password

## And run the tests!!

    mvn test

# Cleanup

## Delete Docker images
...

# Challenges

## Smack Api changes

## Vines differing handling of multiple connections

## Impedance mismatch between Mockito and JMock

# Key lessons

## Learning is key, not the code. That's why I deleted it all!!

## Disable debugging - run tests that fail to show failure is easy to follow

## Keeping the tests real simple and lean

## Defining variables in unit tests final!

## Loose verifications

    verify(auction, times(1)).bid(price + increment);
    verify(sniperListener, atLeastOnce()).sniperBidding();

Although didn't fail when introducing show winning functionality

## The whole JMock allowances / status functionality

## The value of a supporting to do list to prevent flow breakage

## Avoid primative obsession

Including use of generics on collections where type is a duplication smell.
