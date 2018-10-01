package com.example.artur.mynotesapp;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private DbOpenHelper mHelper;
    private SimpleCursorAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), NoteEdit.class);
                startActivity(intent);
            }
        });

        ListView notesList = findViewById(R.id.list);
        notesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textContainer = view.findViewById(R.id.noteText);
                TextView titleContainer = view.findViewById(R.id.noteTitle);

                ClipboardManager clipboard =
                        (ClipboardManager) getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("", textContainer.getText().toString());
                if (clipboard != null) {
                    clipboard.setPrimaryClip(clip);
                }

                Toast.makeText(getApplicationContext(),
                        getString(R.string.copied_note_prefix) + " " +
                                titleContainer.getText().toString() + " " +
                                getString(R.string.copied_note_postfix), Toast.LENGTH_LONG).show();
            }
        });

        mHelper = new DbOpenHelper(this);
        mAdapter = mHelper.getAdapter();
        ((ListView) findViewById(R.id.list)).setAdapter(mAdapter);

        registerForContextMenu(notesList);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHelper != null) {
            mHelper.close();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        showNotes();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_note, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch (item.getItemId()) {
            case R.id.note_edit:
                Intent intent = new Intent(getApplicationContext(), NoteEdit.class);
                intent.putExtra(DbOpenHelper.EXTRA_NOTE_ID, (int) info.id);
                startActivity(intent);
                return true;
            case R.id.note_delete:
                mHelper.deleteNote(info.id);
                showNotes();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void showNotes() {
        mAdapter.swapCursor(mHelper.getCursor());
    }
}
