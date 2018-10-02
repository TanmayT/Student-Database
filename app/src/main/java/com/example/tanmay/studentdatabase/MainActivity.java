package com.example.tanmay.studentdatabase;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity implements OnClickListener
{
    EditText Rollno,Name,Att,Email;
    Button Insert,Delete,Update,View,ViewAll,ClearAll,lessAtt,SendMail;
    SQLiteDatabase db;
    String str ="";
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Rollno=(EditText)findViewById(R.id.Rollno);
        Name=(EditText)findViewById(R.id.Name);
        Att=(EditText)findViewById(R.id.Att);
        Email=(EditText)findViewById(R.id.email);
        Insert=(Button)findViewById(R.id.Insert);
        Delete=(Button)findViewById(R.id.Delete);
        Update=(Button)findViewById(R.id.Update);
        View=(Button)findViewById(R.id.View);
        ViewAll=(Button)findViewById(R.id.ViewAll);
        ClearAll=(Button)findViewById(R.id.ClearDatabase);
        lessAtt=(Button)findViewById(R.id.lessAtt);
        SendMail=(Button)findViewById(R.id.sendmail);

        Insert.setOnClickListener(this);
        Delete.setOnClickListener(this);
        Update.setOnClickListener(this);
        View.setOnClickListener(this);
        ViewAll.setOnClickListener(this);
        ClearAll.setOnClickListener(this);
        lessAtt.setOnClickListener(this);
        Email.setOnClickListener(this);
        SendMail.setOnClickListener(this);


        // Creating database and table
        db=openOrCreateDatabase("StudentDB", Context.MODE_PRIVATE, null);

        db.execSQL("CREATE TABLE IF NOT EXISTS student(rollno VARCHAR,name VARCHAR,att VARCHAR,email VARCHAR);");

    }
    public void onClick(View view)
    {
        // Inserting a record to the Student table
        if(view==Insert)
        {
            // Checking for empty fields
            if(Rollno.getText().toString().trim().length()==0||
                    Name.getText().toString().trim().length()==0||
                    Att.getText().toString().trim().length()==0||
                    Email.getText().toString().trim().length()==0 )
            {
                showMessage("Error", "Please enter all values");
                return;
            }
            db.execSQL("INSERT INTO student VALUES('"+Rollno.getText()+"','"+Name.getText()+
                    "','"+Att.getText()+"','"+Email.getText()+"');");
            showMessage("Success", "Record added");
            clearText();
        }

        // Deleting a record from the Student table
        if(view==Delete)
        {
            // Checking for empty roll number
            if(Rollno.getText().toString().trim().length()==0)
            {
                showMessage("Error", "Please enter Rollno");
                return;
            }
            Cursor c=db.rawQuery("SELECT * FROM student WHERE rollno='"+Rollno.getText()+"'", null);
            if(c.moveToFirst())
            {
                db.execSQL("DELETE FROM student WHERE rollno='"+Rollno.getText()+"'");
                showMessage("Success", "Record Deleted");
            }
            else
            {
                showMessage("Error", "Invalid Rollno");
            }
            clearText();
        }

        // Updating a record in the Student table
        if(view==Update)
        {
            // Checking for empty roll number
            if(Rollno.getText().toString().trim().length()==0)
            {
                showMessage("Error", "Please enter Rollno");
                return;
            }
            Cursor c=db.rawQuery("SELECT * FROM student WHERE rollno='"+Rollno.getText()+"'", null);
            if(c.moveToFirst()) {
                db.execSQL("UPDATE student SET name='" + Name.getText() + "',att='" + Att.getText() +
                        "',email='" + Email.getText() + "' WHERE rollno='"+Rollno.getText()+"'");
                showMessage("Success", "Record Modified");
            }
            else {
                showMessage("Error", "Invalid Rollno");
            }// invalid entry
            clearText();
        }

        // Display a record from the Student table
        if(view==View)
        {
            // Checking for empty roll number
            if(Rollno.getText().toString().trim().length()==0)
            {
                showMessage("Error", "Please enter Rollno");
                return;
            }
            Cursor c=db.rawQuery("SELECT * FROM student WHERE rollno='"+Rollno.getText()+"'", null);
            if(c.moveToFirst())
            {
                Name.setText(c.getString(1));
                Att.setText(c.getString(2));
                Email.setText(c.getString(3));
            }
            else
            {
                showMessage("Error", "Invalid Rollno");
                clearText();
            }
        }

        // Displaying all the records
        if(view==ViewAll)
        {
            Cursor c=db.rawQuery("SELECT * FROM student", null);
            if(c.getCount()==0)
            {
                showMessage("Error", "No records found");
                return;
            }
            StringBuffer buffer=new StringBuffer();
            while(c.moveToNext())
            {
                buffer.append("Rollno: "+c.getString(0)+"\n");
                buffer.append("Name: "+c.getString(1)+"\n");
                buffer.append("Attendance: "+c.getString(2)+"\n\n");
                buffer.append("Email Address: "+c.getString(3)+"\n\n");
            }
            showMessage("Student Details", buffer.toString());
        }

        // clean full database
        if(view==ClearAll)
        {
            db.execSQL("delete from "+"student");
            showMessage("Success", "Database Cleared");
        }

        //students with attendance less than 75 percent
        if(view==lessAtt)
        {
            Cursor c=db.rawQuery("SELECT * FROM student WHERE att<'75'", null);
            if(c.getCount()==0)
            {
                showMessage("Error", "No records found");
                return;
            }
            StringBuffer buffer=new StringBuffer();
            while(c.moveToNext())
            {
                buffer.append("Rollno: "+c.getString(0)+"\n");
                buffer.append("Name: "+c.getString(1)+"\n");
                buffer.append("Attendance: "+c.getString(2)+"\n\n");
                buffer.append("Email Address: "+c.getString(3)+"\n\n\n");
            }
            showMessage("Student Details", buffer.toString());
        }

        //send email to students with less than 75 percent attendance
        if(view==SendMail)
        {
            Cursor c=db.rawQuery("SELECT email FROM student WHERE att<'75'", null);
            if(c.getCount()==0)
            {
                showMessage("SORRY :(", "No student has attendance less than 75 percent");
                return;
            }

                while(c.moveToNext()){
                    str = str+c.getString(0)+",";
                }
                sendMail(str);
                str="";
        }
    }

    private void showMessage(String title,String message)
    {
        Builder builder=new Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }

    private void clearText()
    {
        Rollno.setText("");
        Name.setText("");
        Att.setText("");
        Email.setText("");
        Rollno.requestFocus();
    }

    private void sendMail(String str) {
        String recipientList = str;
        String[] recipients = recipientList.split(",");

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, recipients);
        intent.putExtra(Intent.EXTRA_SUBJECT,getString(R.string.subject));
        intent.putExtra(Intent.EXTRA_TEXT,getString(R.string.message));

        intent.setType("message/rfc822");
        startActivity(Intent.createChooser(intent, "Choose an email client"));
    }

}

