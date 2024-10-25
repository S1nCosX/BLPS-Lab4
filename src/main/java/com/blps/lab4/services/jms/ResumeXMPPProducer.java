package com.blps.lab4.services.jms;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;

public class ResumeXMPPProducer {

    private XMPPTCPConnection connection;

    public ResumeXMPPProducer(String server, int port, String username, String password) throws Exception {
        XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
                .setXmppDomain(server)
                .setHost(server)
                .setPort(port)
                .setUsernameAndPassword(username, password)
                .setSecurityMode(ConnectionConfiguration.SecurityMode.ifpossible)
                .build();

        connection = new XMPPTCPConnection(config);
        connection.connect();
        connection.login();
    }

    public void sendMessage(String queueName, String message) throws Exception {
        ChatManager chatManager = ChatManager.getInstanceFor(connection);
        EntityBareJid jid = JidCreate.entityBareFrom(queueName + "@" + connection.getXMPPServiceDomain());
        Chat chat = chatManager.chatWith(jid);
        chat.send(message);
        System.out.println("Message sent: " + message);
    }

    public void disconnect() {
        if (connection != null && connection.isConnected()) {
            connection.disconnect();
        }
    }
}
