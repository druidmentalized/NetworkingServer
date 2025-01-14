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

## Usage
### Commands
- `/help` — shows menu with all commands to the client
- `/rules` — shows server rules
- `/bannedWords` — shows banned words
- `/nickname` — sets your new nickname
- `/dm <usernickname> <message>` — addressing message to a specific user
- `/dm <usernickname1>, <usernickname2> <message>` — addressing message to group of specific users
- `/em <usernickname> <message>` — addressing message to anyone else but this user
- `/em <usernickname1>, <usernickname2> <message>` — addressing message to everyone else but group of specific users
- `/exit` — leaving the server
- Writing plain text will result in messaging using global chat available to all users.
### Server Side
- After server start server output welcome message in the console and starts to wait for new clients. Example:
    ```
    Welcome to the server!
    Loading config from: /config.txt
    Config successfully loaded
    Server UTPProject2 ready to accept connections with port 5058
    ```
- All client connections and message sending is logged on the server. Example:
    ```
    Connected user from /127.0.0.1:50828
    Connected user from /127.0.0.1:50834
    Message from User1: Hello World!
    Message from User2: What a good evening it is!
    ```
### Client Side
- After connection to the opened server. User is welcomed with a message and commands he can output, connected clients are shown. Example:
    ```
    Welcome to the UTPProject2 server!
    Your current nickname: User2. In order to change it lookup in the commands section
    --------------------------------------------------------------------------------------------
    Our server focuses only on polite and respectful communication, so
    we kindly ask you to follow the rules written below:
    1. Be Respectful and Kind
    2. Use Appropriate Language
    3. Stay On-Topic
    4. Protect Privacy
    5. No Harassment or Bullying
    6. Follow Moderators’ Guidance
    7. Enjoy and Contribute Positively
    --------------------------------------------------------------------------------------------
    Commands available on the server:
    /help -- show this menu
    /rules -- shows server rules
    /bannedwords -- shows banned words
    /nickname <usernickname> -- sets your new nickname
    /dm <usernickname> -- addressing message to a specific user
    /dm <usernickname1>, <usernickname2>, <...> -- addressing message to group of specific users
    /em <usernickname> -- addressing message to everyone else but this user
    /em <usernickname1>, <usernickname2>, <...> -- addressing message to everyone else but group of specific users
    /exit -- leaving the server
    Writing plain text will result in messaging using global chat available to all users
    --------------------------------------------------------------------------------------------
    Currently connected clients:
    User1
    User2 (you)
    ```
- After getting some message, private or global it is shown on the user chat. Example:
    ```
    Last messages:
    [User1 -> Global] Hello World!
    [User2 -> Global] What a good evening it is!
    [User1 -> User2] Hello my friend!
    [User2 -> User1] Hello you too!
    ```

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
