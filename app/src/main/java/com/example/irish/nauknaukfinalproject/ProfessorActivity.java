package com.example.irish.nauknaukfinalproject;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

/**
 * Professor Activity class. In this class, the user is presented a Professor's 'page', if you will.
 * On this page is the professor's name, department, office location, office hours, phone number, and
 * email address. Phone number and email address fields also have images next to the side, which, upon
 * being clicked, will call or email the professor accordingly.
 *
 * Additionally, in the menu, the user has the a Star button to Favorite or un-Favorite the professor whose
 * page they are currently looking at. Clicking this Star button will favorite or un-favorite the professor,
 * depending on whether or not they are already in the user's favorites. This star image is either hollow,
 * signifying the professor is not in the user's favorites, or filled, signifying the professor is in fact
 * in their favorites.
 *
 * Sources:
 *      Intents and Intent Filters, Android Developers
 *      https://developer.android.com/guide/components/intents-filters.html
 *
 * Version: 1.2
 * Authors: Jason Conci, Daniel Abrahms
 */

public class ProfessorActivity extends AppCompatActivity {
    private final String TAG = "PROFESSOR_ACTIVITY";
    public static String EMAIL_KEY = "email";
    public static String PASSWORD_KEY = "password";
    public static String FIRSTNAME_KEY = "firstName";
    public static String LASTNAME_KEY = "lastName";
    public static String DEPARTMENT_KEY = "department";
    public static String AVAILABLE_KEY = "isAvailable";
    public static String PHONE_NUMBER_KEY = "phoneNumber";
    public static String OFFICE_LOCATION_KEY = "officeLocation";
    public static String OFFICE_HOURS_KEY = "officeHours";
    public static String ROOT_KEY = "NaukNauk";
    public static String USERS_KEY = "NaukNauk/Users";
    public static String STUDENTS_KEY = "NaukNauk/Users/Students";
    public static String PROFESSORS_KEY = "NaukNauk/Users/Professors";

    // Fields, from which we display Professor information and launch implicit intents
    private String currentUser = "jconci@zagmail.gonzaga.edu";
    private Professor professor;
    boolean isAvailable;
    // Firestore Collection References, used in Favorites addition/deletion
    private FirebaseFirestore db = null;
    private CollectionReference rootReference = null;
    private CollectionReference studentCollectionRef = null;
    private CollectionReference professorCollectionRef = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professor);

        // Gathering references to our FireStore root, Professor, and Student Collections.
        this.db = FirebaseFirestore.getInstance();
        this.rootReference = db.collection(ROOT_KEY);
        this.professorCollectionRef=db.collection(PROFESSORS_KEY);
        this.studentCollectionRef = db.collection(STUDENTS_KEY);

        Intent intent = getIntent();

        if(intent != null){

            // Gathering Professor fields from our Intent
            String firstName = intent.getStringExtra(FIRSTNAME_KEY);
            String lastName = intent.getStringExtra(LASTNAME_KEY);
            String email = intent.getStringExtra(EMAIL_KEY);
            String department = intent.getStringExtra(DEPARTMENT_KEY);
            String officeLocation = intent.getStringExtra(OFFICE_LOCATION_KEY);
            String officeHours = intent.getStringExtra(OFFICE_HOURS_KEY);
            String phoneNumber = intent.getStringExtra(PHONE_NUMBER_KEY);
            isAvailable = intent.getBooleanExtra(AVAILABLE_KEY, false);

            // Creating a new Professor object with these names. password field is 'dummy' since
            // we haven't yet implemented User Authentication. In the future this will not be the case.
            professor = new Professor(firstName, lastName, email, "dummy", department, officeLocation, officeHours, phoneNumber);
            Log.d(TAG, professor.toString());

            // Gathering our widgets, with which we will display Professor information to the user
            TextView nameText = findViewById(R.id.ProfessorNameTextView);
            TextView departmentText = findViewById(R.id.DepartmentTextView);
            TextView emailText = findViewById(R.id.EmailTextView);
            TextView officeLocationText = findViewById(R.id.OfficeLocationTextView);
            TextView phoneNumberText = findViewById(R.id.PhoneNumberTextView);
            TextView officeHoursText = findViewById(R.id.OfficeHoursTextView);

            nameText.setText(professor.getFirstName() + " " + professor.getLastName());
            departmentText.setText(professor.getDepartment());
            emailText.setText(professor.getEmail());
            officeLocationText.setText((professor.getOfficeLocation()!=null) ? professor.getOfficeLocation() : "Office Location N/A");
            phoneNumberText.setText((professor.getPhoneNumber()!=null) ? professor.getPhoneNumber() : "Phone Number N/A");
            officeHoursText.setText((professor.getOfficeHours()!=null) ? professor.getOfficeHours() : "Office Hours N/A");
        }


        /**
         * In this block of code, we start an implicit intent to send to an email to the Professor
         * at the email address listed on the Professor's page, which we know is valid, since it is
         * their login ID.
         *
         */
        ImageView emailButton = (ImageView) findViewById(R.id.EmailImageButton);
        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("mailto: " + professor.getEmail()).buildUpon().build();
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, uri);
                startActivity(Intent.createChooser(emailIntent, "Choose how you'd like to send"));
            }
        });

        /**
         * In this block of code, we start an implicit intent to call the phone number listed on
         * the current Professor's page, assumedly their office phone number.
         */
        ImageView phoneButton = (ImageView) findViewById(R.id.PhoneNumberImageButton);
        phoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                if (professor.getPhoneNumber().equals("Phone Number N/A")) {
                    Snackbar.make(findViewById(R.id.professorLayout), "", Snackbar.LENGTH_SHORT).show();
                }
                else if (professor.getPhoneNumber().substring(0,9).equals("(509) 313")) {
                    intent.setData(Uri.parse("tel:" + professor.getPhoneNumber()));
                } else {
                    intent.setData(Uri.parse("tel:509313" + professor.getPhoneNumber()));
                }
                if(intent.resolveActivity(getPackageManager()) != null){
                    startActivity(intent);
                }
            }
        });

        TextView availableBar = (TextView) findViewById(R.id.ProfessorAvailableTextView);
        if(isAvailable) {
            availableBar.setText(R.string.available_true);
            availableBar.setBackgroundResource(R.color.available_true);
        }
        else {
            availableBar.setText(R.string.available_false);
            availableBar.setBackgroundResource(R.color.available_false);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    /**
     * Method to determine whether or not we put the 'add to favorites' or 'delete from favorites'
     * menu option in our Menu.
     *
     * The method for determining this is to access the current user's favorites list, and check whether
     * or not the current professor is in the list or not. If they are not, display the add button;
     * else, display the delete button.
     *
     * @param menu -> the Menu in which we will inflate our R.menu file
     * @return -> a boolean as to whether or not this action was successful
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final Menu menuFinal = menu;
        studentCollectionRef.document(currentUser).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    Student student = task.getResult().toObject(Student.class);
                    List<DocumentReference> favoritesList = student.getFavorites();
                    // If the current professor is not in the current user's Favorites list
                    if(!favoritesList.contains(professorCollectionRef.document(professor.getEmail()))){
                        MenuInflater balloon = getMenuInflater();
                        balloon.inflate(R.menu.professor_menu_add, menuFinal);
                    }
                    // Else, the current professor is in the current user's Favorites list
                    else {
                        MenuInflater balloon = getMenuInflater();
                        balloon.inflate(R.menu.professor_menu_delete, menuFinal);
                    }
                }
            }
        });
        return super.onCreateOptionsMenu(menu);
    }


    /**
     * In this method, we handle user selections within our Menu.
     *
     * Clicking the home button returns the user to the Activity that started this Activity
     * (Not specified because it could be either Favorites or Search Activities)
     *
     * Clicking on the 'add to favorites' button adds the current professor to the current user's
     * list of Favorites. Clicking the 'delete from favorites' accomplishes the inverse.
     * See documentation for onCreateOptionsMenu for implementation of Menu inflation.
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                ProfessorActivity.this.finish();
                return true;
            case R.id.addProfessorToFavorites:
                addProfessorToFavorites();
                return true;
            case R.id.deleteProfessorFromFavorites:
                deleteProfessorFromFavorites();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Method to add the current professor to the current user's favorites.
     * This is accomplished by gathering a DocumentReference to our current user, and casting this
     * user to a Student object. From this, we gather the Student's List<DocumentReference> field
     * of Favorites. Once done, we gather the current Professor's DocumentReference, and add this
     * DocumentReference to the Student's list of Favorites.
     *
     * Since, at this point, we've only copied the values from our Student, we need to write the new
     * values back into the database. This is accomplished by setting the document currentUser equal to
     * the Student object gathered above, with modified Favorites List. On success, the User is reassured
     * with a Snackbar, showing that the Favorites Addition was successful.
     */
    private void addProfessorToFavorites(){
        // Gathering our current user
        studentCollectionRef.document(currentUser).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    // Casting the current user into a Student object
                    Student student = task.getResult().toObject(Student.class);
                    Log.d(TAG, student.toString());
                    // Gathering the Student's list of Favorites
                    List<DocumentReference> favoritesList = student.getFavorites();
                    // If the Student's list of Favorites does not already contain the current professor
                    if (!favoritesList.contains(professorCollectionRef.document(professor.getEmail()))) {
                        // We gather the current professor's DocumentReference, and add it to the user's
                        // Favorites list.
                        favoritesList.add(professorCollectionRef.document(professor.getEmail()));
                        student.setFavorites(favoritesList);
                        // Then, we get the DocumentReference for our current user, and set it equal to
                        // our new Student object with modified Favorites list
                        studentCollectionRef.document(currentUser).set(student).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.d(TAG, "It's working here");
                                Snackbar.make(findViewById(R.id.professorLayout), "Favorites Addition Successful!", Snackbar.LENGTH_SHORT).show();
                                invalidateOptionsMenu();
                            }
                        });
                    } else {
                        // Professor already in FavoritesList
                        Snackbar.make(findViewById(R.id.professorLayout), "Professor Already in Favorites", Snackbar.LENGTH_SHORT).show();
                    }
                }else{
                    // Unexpected error, gathering Student DocumentReference was not successful
                    Log.d(TAG, "Error Adding Professor");
                }
            }
        });
    }

    /**
     * Method to delete the current professor from the current user's favorites.
     * This is accomplished by gathering a DocumentReference to our current user, and casting this
     * user to a Student object. From this, we gather the Student's List<DocumentReference> field
     * of Favorites. Once done, we gather the current Professor's DocumentReference, and delete this
     * DocumentReference from the Student's Favorites List.
     *
     * Since, at this point, we've only copied the values from our Student, we need to write the new
     * values back into the database. This is accomplished by current user's Student document equal to
     * the Student object above, with modified Favorites List. On success, the User is reassured
     * with a Snackbar, showing that the Favorites Deletion was successful.
     */
    public void deleteProfessorFromFavorites(){
        // Gathering the document for our current user
        studentCollectionRef.document(currentUser).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                // If all goes well
                if(task.isSuccessful()){
                    // Cast this document to a Student object
                    Student student = task.getResult().toObject(Student.class);
                    Log.d(TAG, student.toString());
                    // Gather the list of Favorites from the Student object
                    List<DocumentReference> favoritesList = student.getFavorites();
                    // If the list of Favorites contains our current professor
                    if (favoritesList.contains(professorCollectionRef.document(professor.getEmail()))) {
                        // We gather the current Professor's DocumentReference, and delete it from
                        // the current user's Favorites list.
                        favoritesList.remove(professorCollectionRef.document(professor.getEmail()));
                        student.setFavorites(favoritesList);
                        // Then, we get the DocumentReference for our current user, and set it equal to our
                        // modified Student object, with our new Favorites list.
                        studentCollectionRef.document(currentUser).set(student).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.d(TAG, "It's working here");
                                Snackbar.make(findViewById(R.id.professorLayout), "Favorites Deletion Successful!", Snackbar.LENGTH_SHORT).show();
                                invalidateOptionsMenu();
                            }
                        });
                    } else {
                        // Professor doesn't exist in FavoritesList
                        Snackbar.make(findViewById(R.id.professorLayout), "Professor Not In Favorites", Snackbar.LENGTH_SHORT).show();
                    }
                }else{
                    // Unexpected error, gathering Student DocumentReference was not successful
                    Log.d(TAG, "Error Adding Professor");
                }
            }
        });
    }
}
