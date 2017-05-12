package com.iambenzo.dailypackt.model;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.iambenzo.dailypackt.util.InvalidLoginException;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by Ben on 28/04/2017.
 */
public class Packt implements Serializable{
    private static final String PACKT_URL = "https://www.packtpub.com";
    private static final String MY_BOOKS_URL = "https://www.packtpub.com/account/my-ebooks";
    private static final String LOGIN_URL = "https://www.packtpub.com/register";
    private static final String FREE_BOOK_URL = "https://www.packtpub.com/packt/offers/free-learning";

    private Map<String, String> cookies;
    private ArrayList<String> obtainedBooks;

    private String bookImage;
    private String bookTitle;
    private String bookDescription;
    private String saveLink;
    private String nid;

    public Packt(String user, String pass, Context context) throws IOException, InvalidLoginException {
        login(LOGIN_URL, user, pass);
        Document doc = Jsoup.connect(FREE_BOOK_URL).cookies(cookies).get();
        bookTitle = doc.select("div.dotd-title > h2").text();
        bookImage = "https:" + doc.select("div.dotd-main-book-image  img.bookimage").first().attr("src");
        bookDescription = doc.select("div.dotd-title ~ br + div").text();
        String claim = doc.select("a.twelve-days-claim").attr("href");
        saveLink = PACKT_URL + claim;
        nid = claim.substring(20, 25);

        obtainedBooks = new ArrayList<>();
        loadObtainedBooks(context);
    }

    private void login(String loginUrl, String email, String password) throws IOException, InvalidLoginException {
        Document loginPage = Jsoup.connect(loginUrl).get();

        //get login form's build ID
        String formBuildID = loginPage.select("input[name=form_build_id]").first().val();

        //perform login via login form
        Connection.Response res = Jsoup.connect(loginUrl)
                    .data("email", email, "password", password, "op", "Login", "form_build_id", formBuildID, "form_id","packt_user_login_form")
                    .method(Connection.Method.POST)
                    .timeout(10000)
                    .execute();

        //Make sure there wasn't an error with logging in
        if(res.parse().select("div#messages-container").html().contains("invalid")){
            throw new InvalidLoginException();
        }

        //Retain our cookies for later use
        cookies = res.cookies();
    }


    public void saveBook() throws IOException {
        Jsoup.connect(saveLink).cookies(cookies).get();
    }

    public void addObtainedBook(String bookTitle, Context context) {
        this.obtainedBooks.add(bookTitle);
        saveObtainedBooks(context);
    }

    private void loadObtainedBooks(Context context) {
        //Loads obtained books from our JSON file
        FileInputStream inputStream;
        Scanner data;
        GsonBuilder builder = new GsonBuilder();
        builder.setLenient();
        Gson gson = builder.create();

        try {
            inputStream = context.openFileInput("obtainedbooks");
            data = new Scanner(inputStream);
            StringBuilder jsonRead = new StringBuilder();
            while(data.hasNext()){
                //Prettify for easier use
                jsonRead.append(data.next() + " ");
            }
            obtainedBooks = gson.fromJson(jsonRead.toString(), new TypeToken<ArrayList<String>>(){}.getType());
        } catch (FileNotFoundException e) {
                e.printStackTrace();
        }
    }

    private void saveObtainedBooks(Context context){
        //Save obtained books list to JSON file
        FileOutputStream outputStream;
        String gson = new GsonBuilder().create().toJson(obtainedBooks);

        try {
            outputStream = context.openFileOutput("obtainedbooks", 0);
            outputStream.write(gson.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getBookImage() {
        return bookImage;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public String getBookDescription() {
        return bookDescription;
    }

    public String getNid() {
        return nid;
    }

    public boolean isObtained(String bookTitle) {
        return obtainedBooks.contains(bookTitle);
    }

    public String getSession(){
        return "SESS_live=" + cookies.get("SESS_live") + ";";
    }

    public String getPdfDownloadLink(String nid){
        return PACKT_URL + "/ebook_download/" + nid + "/pdf";
    }

    public String getMobiDownloadLink(String nid){
        return PACKT_URL + "/ebook_download/" + nid + "/mobi";
    }

    public String getePubDownloadLink(String nid){
        return PACKT_URL + "/ebook_download/" + nid + "/epub";
    }
}

