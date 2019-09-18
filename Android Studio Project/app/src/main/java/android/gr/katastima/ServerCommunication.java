package android.gr.katastima;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.people.v1.People;
import com.google.api.services.people.v1.model.Birthday;
import com.google.api.services.people.v1.model.Date;
import com.google.api.services.people.v1.model.Gender;
import com.google.api.services.people.v1.model.Person;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class ServerCommunication {

    public static String BASE_URL;

    public static void init() {
        try {
            FileInputStream in = new FileInputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/pc_ip.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String ret = "http://" + br.readLine() + ":3000/";
            BASE_URL = ret;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class PointSetter extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            HttpURLConnection con = null;
            BufferedReader br = null;

            try {
                final String base = ServerCommunication.BASE_URL + "api/addPoints/?";
                final String email = "email";
                final String appId = "appId";
                final String userId = "userId";
                final String points = "points";

                Uri builtUri = Uri.parse(base).buildUpon()
                        .appendQueryParameter(email, params[0])
                        .appendQueryParameter(appId, params[1])
                        .appendQueryParameter(userId, params[2])
                        .appendQueryParameter(points, params[3])
                        .build();

                URL url = new URL(builtUri.toString());

                Log.i("PointGetter", builtUri.toString());

                con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.connect();

                InputStream in = con.getInputStream();
                StringBuffer buffer = new StringBuffer();

                if(in == null) return null;

                br = new BufferedReader(new InputStreamReader(in));

                String line;
                while ((line = br.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if(buffer.length() == 0) return  null;
                else return  buffer.toString();

            }
            catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if(s != null) {
                super.onPostExecute(s);

                String[] tokens = s.split("/");

                if(tokens[0].equals("success")) {
                    Toast.makeText(MainActivity.context, "Successful check in. +5 points", Toast.LENGTH_SHORT).show();
                    MainActivity.CUR_POINTS = Integer.parseInt(tokens[1].trim());
                    MainActivity.updatePointTxtView();
                }
                else if(tokens[0].trim().equals("early")) {
                    Toast.makeText(MainActivity.context, "You've already checked in. Try again tomorrow.", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(MainActivity.context, "Try again..", Toast.LENGTH_LONG).show();
                }
            }
            else Log.e("AddPoints", "NULL");
        }

    }

    public static class PointGetter extends AsyncTask<String, Void, String> {

        private boolean update;

        public PointGetter(boolean update) {
            this.update = update;
        }

        @Override
        protected String doInBackground(String... params) {

            HttpURLConnection con = null;
            BufferedReader br = null;

            try {
                final String base = ServerCommunication.BASE_URL + "api/getPoints/?";
                final String email = "email";
                final String appId = "appId";
                final String userId = "userId";

                Uri builtUri = Uri.parse(base).buildUpon()
                        .appendQueryParameter(email, params[0])
                        .appendQueryParameter(appId, params[1])
                        .appendQueryParameter(userId, params[2])
                        .build();

                URL url = new URL(builtUri.toString());

                Log.i("PointGetter", builtUri.toString());

                con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.connect();

                InputStream in = con.getInputStream();
                StringBuffer buffer = new StringBuffer();

                if(in == null) return null;

                br = new BufferedReader(new InputStreamReader(in));

                String line;
                while ((line = br.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if(buffer.length() == 0) return  null;
                else return  buffer.toString();

            }
            catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if(s != null) {
                super.onPostExecute(s);

                String[] tokens = s.split("/");
                if(tokens[0].equals("success")) {
                    MainActivity.CUR_POINTS = Integer.parseInt(tokens[1].trim());
                    if(update) {
                        MainActivity.updatePointTxtView();
                    }
                }
                else Toast.makeText(MainActivity.context, "Try again..", Toast.LENGTH_LONG).show();
            }
            else Log.e("PointGet", "NULL");
        }
    }

    public static class CodeCreation extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String codeString = Functions.constructCode(MainActivity.context);

            HttpURLConnection con = null;
            BufferedReader br = null;

            try {
                final String base = ServerCommunication.BASE_URL + "api/codeCreation/?";
                final String email = "email";
                final String code = "code";
                final String appId = "appId";
                final String userId = "userId";
                final String points = "points";

                Uri builtUri = Uri.parse(base).buildUpon()
                        .appendQueryParameter(code, codeString)
                        .appendQueryParameter(email, params[0])
                        .appendQueryParameter(appId, params[1])
                        .appendQueryParameter(userId, params[2])
                        .appendQueryParameter(points, params[3])
                        .build();

                URL url = new URL(builtUri.toString());

                Log.i("CodeCreation", builtUri.toString());

                con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.connect();

                InputStream in = con.getInputStream();
                StringBuffer buffer = new StringBuffer();

                if(in == null) return null;

                br = new BufferedReader(new InputStreamReader(in));

                String line;
                while ((line = br.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if(buffer.length() == 0) return  null;
                else return  buffer.toString();

            }
            catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if(s != null) {
                super.onPostExecute(s);

                String[] tokens = s.split("/");
                if(tokens[0].equals("success")) {
                    MainActivity.CUR_POINTS = Integer.parseInt(tokens[1].trim());
                    MainActivity.updatePointTxtView();
                    MainActivity.activity.startActivity(new Intent(MainActivity.context, CodePopClass.class));
                }
                else Toast.makeText(MainActivity.context, "Try again..", Toast.LENGTH_LONG).show();
            }
            else Log.e("Code", "NULL");
        }
    }

    public static class TokenAuthentication extends AsyncTask<String, Void, String> {

        private MainActivity ma;

        public TokenAuthentication(MainActivity ma) {
            this.ma = ma;
        }

        @Override
        protected String doInBackground(String... params) {

            HttpURLConnection con = null;
            BufferedReader br = null;

            try {
                final String base = ServerCommunication.BASE_URL + "api/tokenAuthentication/?";
                final String token = "tokenId";
                final String email = "email";
                final String appId = "appId";
                final String userId = "userId";
                final String gender = "gender";
                final String birthday = "birthday";



                Uri builtUri = Uri.parse(base).buildUpon()
                        .appendQueryParameter(token, params[0])
                        .appendQueryParameter(email, params[1])
                        .appendQueryParameter(appId, params[2])
                        .appendQueryParameter(userId, params[3])
                        .appendQueryParameter(gender, params[4])
                        .appendQueryParameter(birthday, params[5])
                        .build();

                URL url = new URL(builtUri.toString());

                Log.i("TokenAuthentication", builtUri.toString());

                con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.connect();

                InputStream in = con.getInputStream();
                StringBuffer buffer = new StringBuffer();

                if(in == null) return null;

                br = new BufferedReader(new InputStreamReader(in));

                String line;
                while ((line = br.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if(buffer.length() == 0) return  null;
                else return buffer.toString();

            }
            catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if(s != null) {
                super.onPostExecute(s);
                ma.updateUi(s.trim());
            }
            else Log.e("RESPONSE", "NULL");

        }

    }

    public static class PeopleTask extends AsyncTask<String, Void, Person> {

        private MainActivity ma;
        private String tokenId;
        private String email;

        public PeopleTask(MainActivity ma, String tokenId, String email) {
            this.ma = ma;
            this.tokenId = tokenId;
            this.email = email;
        }

        @Override
        protected Person doInBackground(String... params) {
            try {
                HttpTransport httpTransport = new NetHttpTransport();
                JacksonFactory jsonFactory = JacksonFactory.getDefaultInstance();

                String redirectUrl = "urn:ietf:wg:oauth:2.0:oob";

                String client_id = ma.getApplicationContext().getString(R.string.server_client_id);
                String client_secret = ma.getApplicationContext().getString(R.string.server_client_secret);

                GoogleTokenResponse tokenResponse = new GoogleAuthorizationCodeTokenRequest(
                        httpTransport,
                        jsonFactory,
                        client_id,
                        client_secret,
                        params[0],
                        redirectUrl).execute();

                GoogleCredential credential = new GoogleCredential.Builder()
                        .setClientSecrets(client_id, client_secret)
                        .setTransport(httpTransport)
                        .setJsonFactory(jsonFactory)
                        .build();


                credential.setFromTokenResponse(tokenResponse);

                People service = new People.Builder(httpTransport, jsonFactory, credential)
                        .setApplicationName(ma.getApplicationContext().getString(R.string.app_name))
                        .build();

                return  service.people()
                        .get("people/me")
                        .setRequestMaskIncludeField("person.genders,person.birthdays")
                        .execute();

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Person person) {
            if(person != null) {
                super.onPostExecute(person);

                String birthday = "undefined";
                String gender = "undefined";

                List<Gender> genders = person.getGenders();
                if(genders != null && genders.size() > 0) {
                    gender = genders.get(0).getValue();
                    Log.e("GENDER", gender);
                }
                else Log.e("GENDERS", "null");

                List<Birthday> birthdays = person.getBirthdays();
                if(birthdays != null && birthdays.size() > 0) {
                    Date d =birthdays.get(0).getDate();
                    birthday = d.getYear() + "-" + d.getMonth() + "-" + d.getDay();
                    Log.e("BIRTHDAY", birthday);
                }

                ServerCommunication.TokenAuthentication task = new ServerCommunication.TokenAuthentication(ma);
                task.execute(
                        tokenId,
                        email,
                        ma.getApplicationContext().getString(R.string.appId),
                        ma.getApplicationContext().getString(R.string.userId),
                        gender,
                        birthday);


            }
        }
    }
}
