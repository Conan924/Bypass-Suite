package burp;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BurpExtender implements IBurpExtender, IContextMenuFactory {

    private IBurpExtenderCallbacks callbacks;

    @Override
    public void registerExtenderCallbacks(IBurpExtenderCallbacks callbacks) {
        this.callbacks = callbacks;
        callbacks.setExtensionName("Bypass Suite");
        callbacks.registerContextMenuFactory(this);
    }

    @Override
    public List<JMenuItem> createMenuItems(IContextMenuInvocation invocation) {
        List<JMenuItem> menuItems = new ArrayList<>();

        JMenuItem encodeMenuItem = new JMenuItem("Unicode Encode");
        encodeMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processSelectedText(invocation, true, false, false, false);
            }
        });
        menuItems.add(encodeMenuItem);

        JMenuItem decodeMenuItem = new JMenuItem("Unicode Decode");
        decodeMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processSelectedText(invocation, false, false, false, false);
            }
        });
        menuItems.add(decodeMenuItem);

        JMenuItem splitMenuItem = new JMenuItem("Split Keyword");
        splitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processSelectedText(invocation, false, true, false, false);
            }
        });
        menuItems.add(splitMenuItem);

        JMenuItem insertGarbageMenuItem = new JMenuItem("Insert Garbage Data");
        insertGarbageMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                insertGarbageData(invocation);
            }
        });
        menuItems.add(insertGarbageMenuItem);

        JMenuItem randomCaseMenuItem = new JMenuItem("Random Case");
        randomCaseMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processSelectedText(invocation, false, false, true, false);
            }
        });
        menuItems.add(randomCaseMenuItem);

        return menuItems;
    }

    private void processSelectedText(IContextMenuInvocation invocation, boolean encode, boolean split, boolean randomCase, boolean insertGarbage) {
        IHttpRequestResponse[] messages = invocation.getSelectedMessages();
        if (messages == null || messages.length == 0) {
            return;
        }

        int[] selectedBounds = invocation.getSelectionBounds();
        if (selectedBounds == null || selectedBounds.length != 2) {
            return;
        }

        byte[] request = messages[0].getRequest();
        String selectedText = new String(request).substring(selectedBounds[0], selectedBounds[1]);
        String processedText = selectedText;

        if (encode) {
            processedText = unicodeEncode(selectedText);
        } else if (split) {
            processedText = splitKeyword(selectedText);
        } else if (randomCase) {
            processedText = randomCase(selectedText);
        } else {
            processedText = unicodeDecode(selectedText);
        }

        byte[] newRequest = new byte[request.length - selectedText.length() + processedText.length()];
        System.arraycopy(request, 0, newRequest, 0, selectedBounds[0]);
        System.arraycopy(processedText.getBytes(), 0, newRequest, selectedBounds[0], processedText.length());
        System.arraycopy(request, selectedBounds[1], newRequest, selectedBounds[0] + processedText.length(), request.length - selectedBounds[1]);

        messages[0].setRequest(newRequest);
    }

    private void insertGarbageData(IContextMenuInvocation invocation) {
        IHttpRequestResponse[] messages = invocation.getSelectedMessages();
        if (messages == null || messages.length == 0) {
            return;
        }

        byte[] request = messages[0].getRequest();
        String requestString = new String(request);

        int numKeyValuePairs = getUserInputForGarbageData();
        if (numKeyValuePairs <= 0) {
            return;
        }

        StringBuilder garbageData = new StringBuilder();
        for (int i = 0; i < numKeyValuePairs; i++) {
            garbageData.append(generateRandomString()).append("=").append(generateRandomString()).append("&");
        }

        // Remove the trailing "&" if it exists
        if (garbageData.length() > 0 && garbageData.charAt(garbageData.length() - 1) == '&') {
            garbageData.setLength(garbageData.length() - 1);
        }

        String newRequestString = requestString + "&" + garbageData.toString();
        messages[0].setRequest(newRequestString.getBytes());
    }

    private int getUserInputForGarbageData() {
        String input = JOptionPane.showInputDialog("Enter the number of key-value pairs to insert:");
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private String generateRandomString() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private String unicodeEncode(String input) {
        StringBuilder unicodeString = new StringBuilder();
        for (char c : input.toCharArray()) {
            unicodeString.append(String.format("\\u%04x", (int) c));
        }
        return unicodeString.toString();
    }

    private String unicodeDecode(String input) {
        StringBuilder decodedString = new StringBuilder();
        String[] unicodeChars = input.split("\\\\u");
        for (int i = 1; i < unicodeChars.length; i++) {
            int code = Integer.parseInt(unicodeChars[i], 16);
            decodedString.append((char) code);
        }
        return decodedString.toString();
    }

    private String splitKeyword(String input) {
        StringBuilder splitString = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            if (i > 0) {
                splitString.append("+");
            }
            splitString.append("'");
            splitString.append(input.charAt(i));
            splitString.append("'");
        }
        return splitString.toString();
    }

    private String randomCase(String input) {
        StringBuilder randomCaseString = new StringBuilder();
        Random random = new Random();
        for (char c : input.toCharArray()) {
            if (random.nextBoolean()) {
                randomCaseString.append(Character.toUpperCase(c));
            } else {
                randomCaseString.append(Character.toLowerCase(c));
            }
        }
        return randomCaseString.toString();
    }
}
