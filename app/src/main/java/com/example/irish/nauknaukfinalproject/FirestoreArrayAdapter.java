package com.example.irish.nauknaukfinalproject;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 * FirestoreArrayAdapter class, subclass of ArrayAdapter<Professor>. In this class, we are given an
 * ArrayList of professor objects, from which we populate the ListView to which this adapter is set.
 * This is done by getting each professor Object, and determining their firstName, lastName, and
 * isAvailable fields. Using activity_list_item, a green or red circle is displayed in the item,
 * based on their availability, as well as the Professor's full name.
 *
 * Sources:
 *      None
 *
 * Version: 1.0
 * Author: Jason Conci
 */

public class FirestoreArrayAdapter extends ArrayAdapter<Professor>{
    public static String EMAIL_KEY = "email";
    public static String PASSWORD_KEY = "password";
    public static String FIRSTNAME_KEY = "firstName";
    public static String LASTNAME_KEY = "lastName";
    public static String DEPARTMENT_KEY = "department";
    public static String AVAILABLE_KEY = "isAvailable";
    public static String FAVORITES_KEY = "favorites";
    public static String PHONE_NUMBER_KEY = "phoneNumber";
    public static String OFFICE_LOCATION_KEY = "officeLocation";
    // Keys for accessing directories, again SQL-style
    public static String ROOT_KEY = "NaukNauk";
    public static String USERS_KEY = "NaukNauk/Users";
    public static String STUDENTS_KEY = "NaukNauk/Users/Students";
    public static String PROFESSORS_KEY = "NaukNauk/Users/Professors";
    // Log tag
    public static String TAG = "FIRESTOREHELPER";

    // Image resources used in creating of Views in getView() method
    private final int[] IMAGES = {R.drawable.greenlight, R.drawable.redlight};

    /**
     * FirestoreArrayAdapter constructor. Takes context, and an ArrayList of Professors as parameter.
     * Uses ArrayAdapter() superclass constructor, passing in context, simple_list_item_1, and professorList
     * as parameters.
     *
     * @param context -> the context in which our FirestoreArrayAdapter is being created
     * @param professorList -> the list of Professors which we want to display
     */
    public FirestoreArrayAdapter(Context context, ArrayList<Professor> professorList){
        super(context, android.R.layout.simple_list_item_1, professorList);
        this.notifyDataSetChanged();
        Log.d(TAG, "List: "+ professorList.toString());
    }


    /**
     * Within this method, we create the view for each item in our Professor ListView.
     * We occupy the activity_list_item TextView, text1, with the Professor's first and last name.
     * Then, we occupy the icon with a green or red image, according to the Professor's availability.
     *
     * @param position -> The activity_list_item's position within the ListView
     * @param convertView -> the activity_list_item itself
     * @param parent -> The ListView this activity_list_item will be placed into
     * @return -> a formatted activity_list_item, which is placed into the ListView
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Professor professor = getItem(position);
        if(convertView==null){
            convertView= LayoutInflater.from(getContext())
                    .inflate(android.R.layout.activity_list_item, parent, false);
        }
        convertView.setMinimumHeight(80);

        TextView txt = (TextView) convertView.findViewById(android.R.id.text1);
        txt.setText(professor.firstName + " " + professor.lastName);
        txt.setTextSize(35);

        ImageView img = (ImageView) convertView.findViewById(android.R.id.icon);
        img.setImageResource(professor.isAvailable ? IMAGES[0] : IMAGES[1]);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(60,60);
        params.gravity = Gravity.CENTER;
        img.setLayoutParams(params);
        return convertView;
    }

    /**
     * Returns number of items inside the current ListView
     * @return -> number of items inside ListView
     */
    @Override
    public int getCount() {
        return super.getCount();
    }
}
