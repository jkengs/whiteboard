# Whiteboard
A whiteboard application that allows multiple users to collaborate in real-time through sockets.

## Features
- Create shapes, lines or erase
- Insert text to annotate or label
- Select from a wide range of colors
- Manage join requests, remove users from the session, save/open existing canvas

## Build
`mvn clean package`

## Usage

Host a Session:

`java -jar <server-jar-file> <port> <username>`

Join an Existing Session:

`java -jar <client-jar-file> <server-address> <server-port> <username>` 
