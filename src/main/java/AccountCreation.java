import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import com.google.gson.*;

interface Callback2{
    void setName(String newName);
}
public class AccountCreation extends JPanel implements Callback2{
    private CreateBox createBox;
    private String createName;
    private createMessage message;

    public AccountCreation(JFrame root){
        // Always call the super constructor for Swing components
        super();

        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;

        // Add a label for plain text
        this.add(new JLabel("Create your new account here:"));

        this.createBox = new CreateBox(this); // Takes reference to 'this' for callback
        c.gridy = 1;
        c.fill = GridBagConstraints.VERTICAL;
        this.add(this.createBox, c);

        this.message = new createMessage(root);
        c.gridy = 2;
        this.add(this.message, c);
    }
    public void setName(String newName){
        this.createName = newName;
        this.message.updateMsg(this.createName);
    }
}

class createMessage extends JLabel {

    // The JSON API we are using (GSON) requires model classes for any
    // object type we expect to receive. This way, GSON can return an
    // easily usable Java class instance after a successful REST API call.
    //
    // IMPORTANT: Instance variable names much match EXACTLY with the JSON
    // object names/keys.
    //
    // We can use a private class here because this type will not be used elsewhere.
    private class StatusJSON{
        String creation;

        public String toString(){
            return creation;
        }
    }


    static final String defaultText = "\n";


    JFrame root;

    // Basic initialization
    public createMessage(JFrame root){
        super();
        this.setLayout(new GridLayout(0,1));
        this.root = root;
        this.setText(defaultText);
    }


    public void updateMsg(String username){
        this.setText(defaultText);
        this.setText(callAPI(username));
        this.root.pack();
        this.setVisible(false);
        this.setVisible(true);
    }


    public String callAPI(String name){
        try {
            // Initialize GSON
            Gson gson = new GsonBuilder().create();
            // Initialize result String

            // Call the API
            URL myURL = new URL("http://localhost:8080/AccountCreation/createAccount?userName=" + name);
            URLConnection api = myURL.openConnection();
            // Call the API (ASYNCHRONOUS)
            HttpURLConnection http = (HttpURLConnection)api;
            http.setRequestMethod("POST"); // PUT is another valid option
            http.setDoOutput(true);
            api.connect();
            BufferedReader in = new BufferedReader(new InputStreamReader(api.getInputStream()));

            // Read from input steam
            StringBuilder b=new StringBuilder();
            b.append(name);
            in.close();
            return  name + " is created successfully!";
        }
        catch (MalformedURLException e) {
            // new URL() failed
            System.out.println("Bad URL...");
        }
        catch (IOException e) {
            // Catches refusals such as 'not found' or 'unauthorized'
            // When the username is already taken, this error will be catched
            return(name + " is already taken" );
        }
        // Default return value
        return "";
    }
}

class CreateBox extends JPanel{
    private JTextField textEntry;
    private JButton submitButton;

    public CreateBox (final Callback2 callback){
        super();
        this.setLayout(new FlowLayout());
        this.textEntry = new JTextField(15);
        this.submitButton = new JButton("Create Account");

        // Defines button click action to call the callback method
        this.submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String text = textEntry.getText();
                callback.setName(text);
            }
        });
        // Defines key enter action to call the callback method
        this.textEntry.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String text = textEntry.getText();
                callback.setName(text);
            }
        });
        // Add subcomponents to this JPanel
        this.add(this.textEntry);
        this.add(this.submitButton);

    }
}

