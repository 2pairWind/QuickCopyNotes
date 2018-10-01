package com.example.artur.mynotesapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class NoteEdit  extends AppCompatActivity {

    private EditText mNoteTitle;
    private EditText mNoteText;
    private DbOpenHelper mHelper;
    private int mNoteId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_edit);

        mHelper = new DbOpenHelper(this);
        mNoteTitle = findViewById(R.id.noteTitle);
        mNoteText = findViewById(R.id.noteText);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(DbOpenHelper.EXTRA_NOTE_ID)) {
            mNoteId = intent.getIntExtra(DbOpenHelper.EXTRA_NOTE_ID, 0);
            String[] note = mHelper.getNoteById(mNoteId);
            mNoteTitle.setText(note[0]);
            mNoteText.setText(note[1]);
        }

        findViewById(R.id.saveBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String noteTitle = mNoteTitle.getText().toString().trim();
                String noteText = mNoteText.getText().toString().trim();
                if (noteTitle.length() > 0 && noteText.length() > 0) {
                    int savingResult = mHelper.saveNote(noteTitle, noteText, mNoteId);
                    if (savingResult > -1) {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.problem_saving_in_db_txt, Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), R.string.fill_all_fields_txt, Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
