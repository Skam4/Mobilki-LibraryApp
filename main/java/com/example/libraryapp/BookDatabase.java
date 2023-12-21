package com.example.libraryapp;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Book.class}, version = 1, exportSchema = false)
public abstract class BookDatabase extends RoomDatabase {

    private static volatile BookDatabase databaseInstance;
    static final ExecutorService databaseWriteExecutor = Executors.newSingleThreadExecutor();

    public abstract BookDao bookDao();

    static BookDatabase getDatabase(final Context context) {
        if (databaseInstance== null)
            databaseInstance = Room.databaseBuilder(context.getApplicationContext(), BookDatabase.class, "book_database")
                    .addCallback(roomDatabaseCallback)
                    .build();
        return databaseInstance;
    }

    private static RoomDatabase.Callback roomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
            databaseWriteExecutor.execute(() -> {
                BookDao dao = databaseInstance.bookDao();
                //dao.deleteAll();

                Book book = new Book("Clean Code", "Robert C. Martin");
                dao.insert(book);
                book = new Book("Latarnik", "Henryk Sienkiewicz");
                dao.insert(book);
                book = new Book("Mały Książę", "Antoine de Saint-Exupéry");
                dao.insert(book);
                book = new Book("Lilije", "Adam Mieckiewicz");
                dao.insert(book);
            });
        }
    };

}
