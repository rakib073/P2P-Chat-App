package com.three.p2p;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static int PERMISSION_REQUEST_STORAGE=1000;
    private static int READ_REQUEST_CODE=42;
    private static int REQUEST_EXTERNAL_STORAGE=10;
    private static  String[] PERMISSIONS_STORAGE={Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE};
    EditText   messageEditText, targetIPEditText;

    List<com.three.p2p.Message>mChat;
    ServerClass serverClass;
    ClientClass clientClass;
    SendReceive sendReceive;

    TextView messageTextViewright,messageTextViewleft;
    String color="#9a19d1";
     String fileName;
     String text;
     RelativeLayout chatBox;
     LayoutInflater layoutInflater;
     LinearLayout linearLayout;
     ListView msgView;
     View left,right;
    static final int MESSAGE_READ=1;
    static  final int  BACKGROUND_COLOR=2;
    static final String TAG = "yourTag";
    //private static  final String FILE_NAME="example.text";
    View view;

    Handler handler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what)
            {
                case MESSAGE_READ:
                    byte[] readBuff= (byte[]) msg.obj;
                    String tempMsg=new String(readBuff,0,msg.arg1);
                    if(tempMsg.charAt(0)=='#' && tempMsg.length()==7) {
                        view.setBackgroundColor(Color.parseColor(tempMsg));

                    }
                    else
                    {
                        //mChat.clear();
                        mChat.add(new com.three.p2p.Message(tempMsg,2));
                        MessageAdapter messageAdapter=new MessageAdapter(MainActivity.this,mChat);
                        msgView.setAdapter(messageAdapter);


                       // chatText.setText(chatText.getText()+"\n                   "+tempMsg);
                    }
                    break;
            }
            return true;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
       // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);





//         Toolbar toolbar=findViewById(R.id.toolbarId);
//         setSupportActionBar(toolbar);
      //  chatBox=(RelativeLayout) findViewById(R.id.chatBoxId);
        mChat=new ArrayList<>();




//        Toast.makeText(getApplicationContext(),"Request Sent",Toast.LENGTH_SHORT).show();
//       clientClass = new ClientClass(getIntent().getStringExtra("IP_ADD").toString(),8888);
//        clientClass.start();


       view=this.getWindow().getDecorView();
       view.setBackgroundResource(R.color.blue);


        messageEditText = findViewById(R.id.messageEditText);
        msgView=(ListView) findViewById(R.id.listviewId);

      //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

       verifyDataFolder();
        verifyStoragePermissions();
//        mChat.add(new com.three.p2p.Message("hello....How are you??",1));
//        mChat.add(new com.three.p2p.Message("hi...i am fine...u??",2));
//        MessageAdapter messageAdapter=new MessageAdapter(MainActivity.this,mChat,color);
//        msgView.setAdapter(messageAdapter);

    }


    private  String readText(String input)
    {
        File file=new File(Environment.getExternalStorageDirectory(),input);
        StringBuilder text=new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while((line=br.readLine())!=null)
            {
               text.append(line);
               text.append("\n");
            }
            br.close();
        }catch (Exception e)
        {
             e.printStackTrace();
        }
       return text.toString();
    }
    private void performFileSearch()
    {
        Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("text/*");
        startActivityForResult(intent,READ_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==READ_REQUEST_CODE && resultCode== Activity.RESULT_OK)
        {
          if(data!=null)
          {
              Uri uri=data.getData();
              String path=uri.getPath();
              path=path.substring(path.indexOf(":")+1);
              if(path.contains("emulated"))
              {
                  path=path.substring(path.indexOf("0")+1);
              }
              Toast.makeText(getApplicationContext(),""+path,Toast.LENGTH_SHORT).show();
              mChat.add(new com.three.p2p.Message(path,1));
              MessageAdapter adapter=new MessageAdapter(MainActivity.this,mChat);
              msgView.setAdapter(adapter);
              sendReceive.write(readText(path).getBytes());
          }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==PERMISSION_REQUEST_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "Permission Granted.", Toast.LENGTH_SHORT).show();
            }else
            {
                Toast.makeText(getApplicationContext(), "Permission Not Granted.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }


        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onStart() {
        super.onStart();
        serverClass = new ServerClass(8888);
        serverClass.start();
        Toast.makeText(getApplicationContext(),"Wating for Client........",Toast.LENGTH_SHORT).show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.menu_layout,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Connect to a server
        if(item.getItemId()==R.id.connectId)
        {
            final EditText editText;
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setTitle("Insert Ip to Connect");
            editText=new EditText(this);
            editText.setHint("Insert Ip to connect");
            editText.setText("192.168.0.");
           builder.setView(editText);
           builder.setPositiveButton("connect", new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialogInterface, int i) {
                   String ip=editText.getText().toString();
                   Toast.makeText(getApplicationContext(),"Request Sent...",Toast.LENGTH_SHORT).show();
                   ClientClass clientClass=new ClientClass(ip,8888);
                   clientClass.start();
               }
           });
           builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialogInterface, int i) {
                   dialogInterface.dismiss();
               }
           });
           AlertDialog dialog=builder.create();
           dialog.show();

        }
// save text file
        if (item.getItemId() == R.id.saveId) {
            final EditText editText;
            // text = chatText.getText().toString();
            AlertDialog.Builder dialog=new AlertDialog.Builder(this);
            dialog.setTitle("Insert File Name");
            editText=new EditText(this);
            dialog.setView(editText);
            dialog.setPositiveButton("submit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                 fileName=editText.getText().toString();
               // Toast.makeText(getApplicationContext(),fileText,Toast.LENGTH_SHORT).show();
                    writeFile(fileName);
                }
            });
            dialog.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                  dialogInterface.dismiss();
                }
            });
            AlertDialog ad=dialog.create();
            ad.show();


        }
            if (item.getItemId() == R.id.red) {
            view.setBackgroundResource(R.color.red);
             String msg="#c9959e";

             sendReceive.write(msg.getBytes());
            }
        if (item.getItemId() == R.id.yellow) {
            view.setBackgroundResource(R.color.yellow);
            String msg="#dedaad";

            sendReceive.write(msg.getBytes());
        }
        if (item.getItemId() == R.id.blue) {
           view.setBackgroundResource(R.color.blue);
            String msg="#8ba4cc";

           sendReceive.write(msg.getBytes());
        }
        if (item.getItemId() == R.id.green) {
           view.setBackgroundResource(R.color.green);
            String msg="#80ba90";

            sendReceive.write(msg.getBytes());
        }
        if (item.getItemId() == R.id.sendFileId) {
              performFileSearch();
        }
            return super.onOptionsItemSelected(item);

    }

    //writting file
    public void writeFile(String fileName)
    {
      String path=Environment.getExternalStorageDirectory().toString();
      File file=null;


      file=new File(path+ "/Peer to Peer/Saved Files",fileName);
      Toast.makeText(getApplicationContext(),file.getAbsolutePath(),Toast.LENGTH_SHORT).show();

      FileOutputStream outputStream;
         try{
             outputStream=new FileOutputStream(file,false);
             for(int i=0;i<mChat.size();i++)
             {
                 String msg="";
                 if(mChat.get(i).getIdentifier()==1)
                 {
                     msg.concat("me : ");
                 }
                 else if(mChat.get(i).getIdentifier()==2)
                 {
                     msg.concat("sender : ");
                 }
                 outputStream.write(msg.getBytes());
                 outputStream.write(mChat.get(i).getMsg().getBytes());
                 outputStream.write("\n".getBytes());
             }
             outputStream.close();
         }catch (FileNotFoundException e)
         {
             e.printStackTrace();
         }catch (IOException e)
         {
             e.printStackTrace();
         }

    }

//sending msg
    public void onSendClicked(View v){
      //  mChat.clear();
        String msg=messageEditText.getText().toString();
        messageEditText.setText("");
        mChat.add(new com.three.p2p.Message(msg,1));
        MessageAdapter adapter=new MessageAdapter(MainActivity.this,mChat);
        msgView.setAdapter(adapter);
        sendReceive.write(msg.getBytes());
    }


    public class ServerClass extends Thread{
        Socket socket;
        ServerSocket serverSocket;
        int port;

        public ServerClass(int port) {
            this.port = port;
        }

        @Override
        public void run() {
            try {
                serverSocket=new ServerSocket(port);
                Log.d(TAG, "Waiting for client...");
                socket=serverSocket.accept();
                Log.d(TAG, "Connection established from server");

//                MainActivity.this.runOnUiThread(new Runnable() {
//
//                    @Override
//                    public void run() {
//                         Toast.makeText(getApplicationContext(),socket.getLocalPort(),Toast.LENGTH_SHORT).show();
//                    }});

                sendReceive=new SendReceive(socket);
                sendReceive.start();
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "ERROR/n"+e);
            }
        }
    }

    private class SendReceive extends Thread{
        private Socket socket;
        private InputStream inputStream;
        private OutputStream outputStream;

        public SendReceive(Socket skt)
        {
            socket=skt;
            try {
                inputStream=socket.getInputStream();
                outputStream=socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            byte[] buffer=new byte[1024];
            int bytes;

            while (socket!=null)
            {
                try {
                    bytes=inputStream.read(buffer);
                    if(bytes>0)
                    {
                        handler.obtainMessage(MESSAGE_READ,bytes,-1,buffer).sendToTarget();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void write(byte[] bytes)
        {
            try {
                outputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public class ClientClass extends Thread{
        Socket socket;
        String hostAdd;
        int port;

        public  ClientClass(String hostAddress, int port)
        {
            this.port = port;
            this.hostAdd = hostAddress;
        }

        @Override
        public void run() {
            try {

                socket=new Socket(hostAdd, port);
                Log.d(TAG, "Client is connected to server");
                sendReceive=new SendReceive(socket);
                sendReceive.start();
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "Can't connect from client/n"+e);
            }
        }
    }

    private void verifyStoragePermissions() {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
        {
            if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(
                        PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE
                );
            }
        }
    }
    private void verifyDataFolder()
    {
        File folder=new File(Environment.getExternalStorageDirectory() + "/Peer to Peer");
        File folder1=new File(folder.getPath() + "/Chattings");
        File folder2=new File(folder.getPath()+ "/Saved Files");
        if(!folder.exists() || !folder.isDirectory())
        {
            folder.mkdir();
            folder1.mkdir();
            folder2.mkdir();
        }
        else if(!folder1.exists())
        {
            folder1.mkdir();
        }
        else if(!folder2.exists())
        {
            folder2.mkdir();
        }
    }

}
