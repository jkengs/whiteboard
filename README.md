# Whiteboard
A whiteboard that allows multiple users to collaborate in real-time through sockets.

## Build
`mvn clean package`

## Usage

Host a Session:

`java -jar <server-jar-file> <port> <username>`

Join an Existing Session:

`java -jar <client-jar-file> <server-address> <server-port> <username>` 
