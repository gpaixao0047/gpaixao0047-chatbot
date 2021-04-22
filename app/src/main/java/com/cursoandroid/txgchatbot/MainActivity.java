package com.cursoandroid.txgchatbot;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.cursoandroid.txgchatbot.Adapter.ChatMessageAdapter;
import com.cursoandroid.txgchatbot.Model.ChatMessage;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.alicebot.ab.AIMLProcessor;
import org.alicebot.ab.Bot;
import org.alicebot.ab.Chat;
import org.alicebot.ab.MagicStrings;
import org.alicebot.ab.PCAIMLProcessorExtension;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    FloatingActionButton btnSend;
    EditText edtTextMsg;
    ImageView imageView;


    private Bot bot;
    public static Chat chat;
    private ChatMessageAdapter adapter;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);
        btnSend = findViewById(R.id.btnSend);
        edtTextMsg = findViewById(R.id.edtTextMsg);
        imageView = findViewById(R.id.imageView);
        adapter = new ChatMessageAdapter(this,new ArrayList<ChatMessage>());
        listView.setAdapter(adapter);
        
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = edtTextMsg.getText().toString();
                String response = chat.multisentenceRespond(edtTextMsg.getText().toString());
                if(TextUtils.isEmpty(message)){
                    Toast.makeText(MainActivity.this, "Menssagem Vazia", Toast.LENGTH_SHORT).show();
                    return;
                }
                sendMessage(message);
                botsReply(response);
                //Limparndo edit text
                edtTextMsg.setText("");
                listView.setSelection(adapter.getCount() - 1);
            }
        });

        boolean available = isSDCartAvaliable();
        AssetManager assets = getResources().getAssets();
        File fileName = new File(getCacheDir().toString().toString() + "/TBC/bots/TBC");
        boolean makeFile = fileName.mkdirs();
        if(fileName.exists()){
            // ler arquivo

            try{
                for(String dir : assets.list("TBC")){
                    File subDir = new File(fileName.getPath() + "/" + dir);
                    boolean subDir_Chek = subDir.mkdirs();

                    for(String file : assets.list("TBC/" + dir)){
                        File newFile = new File(fileName.getPath() + "/" + dir + "/" + file);
                        if(newFile.exists()){
                            continue;
                        }
                        InputStream in;
                        OutputStream out;
                        String str;
                        in = assets.open("TBC/" + dir + "/" + file);
                        out = new FileOutputStream(fileName.getPath() + "/" + dir + "/" + file);

                        // copiando files para cartao de memoria secundario

                        copyFile(in,out);
                        in.close();
                        out.flush();
                        out.close();

                    }
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }

        // Pegando diretorio
        MagicStrings.root_path = getCacheDir().toString() + "/TBC";
        AIMLProcessor.extension = new PCAIMLProcessorExtension();
        bot = new Bot("TBC", MagicStrings.root_path, "chat");
        chat = new Chat(bot);

        findViewById(R.id.imageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            exibirCaixaConfirmacao();
            }
        });

    }

    public void exibirCaixaConfirmacao(){
        AlertDialog.Builder msgBox = new AlertDialog.Builder(this);
        msgBox.setTitle("Criador Do Aplicativo");
        msgBox.setIcon(android.R.drawable.ic_menu_info_details);
        msgBox.setMessage("Desenvolvido por Guilherme Paixão");
        msgBox.setPositiveButton("Fechar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this, "Estou fechando...", Toast.LENGTH_SHORT);
            }
        });
        msgBox.show();
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != - 1){
            out.write(buffer,0,read);
        }
    }

    public static boolean isSDCartAvaliable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ? true : false;
    }

    private void botsReply(String response) {
        ChatMessage chatMessage = new ChatMessage(true, false,response);
        adapter.add(chatMessage);
    }

    private void sendMessage(String message) {
        ChatMessage chatMessage = new ChatMessage(false, true,message);
        adapter.add(chatMessage);
    }
}