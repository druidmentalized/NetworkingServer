## Networking Server
This project is a Networking Server built in Java, enabling real-time communication between users over a TCP connection. 
The server operates through a command-line interface, allowing users to connect and send various types of messages using predefined commands.

## Features
- TCP-Based communication
- Direct messaging
- Exclude messaging
- Custom user nicknames
- Banned words & phrases control
- Clear help & rules section

## Commands
- `/help` — shows menu with all commands to the client
- `/rules` — shows server rules
- `/bannedWords` — shows banned words
- `/nickname` — sets your new nickname
- `/dm <usernickname> <message>` — addressing message to a specific user
- `/dm <usernickname1>, <usernickname2> <message>` — addressing message to group of specific users
- `/em <usernickname> <message>` — addressing message to anyone else but this user
- `/em <usernickname1>, <usernickname2> <message>` — addressing message to everyone else but group of specific users
- `/exit` — leaving the server
- Writing plain text will result in messaging using global chat available to all users

## Built with
- Java and Java.net
- Flexible user control

## Project Structure
- `Server.java` - Entry point of the server application
- `Client.java` - Entry point of the client application
- `src/main/java/client` - Directory with client classes
- `src/main/java/server` - Directory with server classes
- `src/main/java/utils` - Directory with utility classes
- `src/main/resources` - Program assets(i.e. config)
- `README.md` - Project documentation
- `LICENSE` - Project license
  
## License
This project is licensed under the MIT License
