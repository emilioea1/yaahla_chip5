package com.chip.parkpro1;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.chip.parkpro1.data.models.Place;
import com.chip.parkpro1.data.models.User;
import com.chip.parkpro1.exceptions.ErrorRequest;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

public class ServerCalls {

    private final static String TAG = ServerCalls.class.getSimpleName();

    private static Message getError(VolleyError error) {
        Message msg = new Message();
        if (error == null || error.networkResponse == null) {
            msg.obj = "Unknown Error";
            return msg;
        }
        switch (error.networkResponse.statusCode) {
            case 500:
                msg.obj = "Error 500: Unexpected error. Internal Server Error. \nRepresents an error that doesn't fall into any other category.";
                break;
            case 400:
                msg.obj = "Error 400: The operation results in a duplicate key for a unique index.";
                break;
            case 406:
                msg.obj = "Error 406: The specified response content type is not supported. \nThe response content type you specified in the request is not supported. ";
                break;
            case 408:
                msg.obj = "Error 408: The request timed out. \n The request timed out. A request times out if it does not complete within 60 seconds.";
                break;
            case 503:
                msg.obj = "Error 503: Application is down for maintenance. \nThe application is down for maintenance. You are advised to stop all requests to it and resume them at a later time.";
                break;
            case 404:
                msg.obj = "Error 404: Item not found. \nThe specified item was not found.";
                break;
            case 415:
                msg.obj = "Error 415: Unsupported Media";
                break;

            default:
                try {
                    NetworkResponse networkResponse = error.networkResponse;
                    if (networkResponse.data != null) {
                        String string = new String(networkResponse.data);
                        @SuppressWarnings({"unchecked", "InstantiatingObjectToGetClassObject"})
                        HashMap<String, String> map = new Gson().fromJson(string, new HashMap<>().getClass());
                        if (map.get("errormessage") != null)
                            msg.obj = map.get("errormessage");
                        else
                            msg.obj = "Connection problem";
                    } else
                        msg.obj = "Connection problem";
                } catch (Exception ex) {
                    msg.obj = ex.getLocalizedMessage();
                    return msg;
                }
        }

        return msg;
    }

    private static int getErrorCode(VolleyError error) {
        try {
            NetworkResponse networkResponse = error.networkResponse;
            if (networkResponse != null && networkResponse.data != null) {
                String string = new String(networkResponse.data);
                @SuppressWarnings({"unchecked", "InstantiatingObjectToGetClassObject"})
                HashMap<String, String> map = new Gson().fromJson(string, new HashMap<>().getClass());
                //noinspection ConstantConditions
                return Integer.parseInt(map.get("errorCode"));
            }
        } catch (Exception ignored) {
        }
        return 0;
    }

    public static ArrayList<Place> getAddresses(Context context, String input) {
        ArrayList<Place> resultList = null;
        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(Constants.PLACES_API_BASE + Constants.TYPE_AUTOCOMPLETE + Constants.OUT_JSON);
            sb.append("?key=").append(context.getString(R.string.google_maps_key));
            sb.append("&types=establishment");
            sb.append("&input=").append(URLEncoder.encode(input, "utf8"));
            sb.append("&location=" + "33.8547, 35.8623");
            sb.append("&radius=" + 500);

            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            return resultList;
        } catch (IOException e) {
            return resultList;
        } finally {
            if (conn != null)
                conn.disconnect();
        }

        try {
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");
            resultList = new ArrayList<>(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                Place place = new Place();
                place.setPlaceID(predsJsonArray.getJSONObject(i).getString("place_id"));
                place.setName(predsJsonArray.getJSONObject(i).getJSONObject("structured_formatting").getString("main_text"));
                resultList.add(place);
            }
        } catch (JSONException ignored) {
        }
        return resultList;
    }

    public static void getPlaceDetails(final Context context, String placeID, final Handler handler) {
        final JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET,
                "https:maps.googleapis.com/maps/api/place/details/json?placeid=" + placeID + "&key="
                        + "AIzaSyAjf6azP5dE50wBk9pMcEABX6WgcLXfb_E", null,
                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Message msg = new Message();
                        msg.arg1 = 1;
                        msg.obj = response;
                        handler.sendMessage(msg);
                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }
        );
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(getRequest);
    }

    private static void refreshToken(final Context context, final Callable<Void> function) {
        final User user = User.getUser(context);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("countryDialCode", user.countryDialCode);
            jsonObject.put("mobile", user.mobile);
        } catch (Exception ignored) {
        }
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.POST, Constants.SERVER_URL + "auth/RefreshToken", null,
                response -> {
                    Log.e("MAJD", "refreshToken onResponse");

                    try {
                        user.token = response.getString("token");
                        User.setUser(context, new Gson().toJson(user));
                        function.call();
                    } catch (Exception ignored) {
                    }
                },
                error -> Log.e("MAJD", "refreshToken onErrorResponse " + error.getMessage())) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("api-version", BuildConfig.VERSION_NAME);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(getRequest);
    }

    public static void checkMobile(Context context, String code, String number, final Handler handler) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("countryDialCode", code);
            jsonObject.put("mobile", number);
        } catch (Exception ignored) {
        }
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.POST, Constants.SERVER_URL + ServerUrl.CHECK_MOBILE.toString(), jsonObject,
                response -> {
                    Message msg = new Message();
                    msg.arg1 = 1;
                    msg.obj = response;
                    handler.sendMessage(msg);
                },
                error -> handler.sendMessage(getError(error))) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("api-version", BuildConfig.VERSION_NAME);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(getRequest);
    }

    public static void requestPin(final Context context, String code, String number, final Callable<Void> function, final Handler handler) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("countryDialCode", code);
            jsonObject.put("mobile", number);
        } catch (Exception ignored) {
        }
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.POST, Constants.SERVER_URL + ServerUrl.REQUEST_PIN.toString(), jsonObject,
                response -> {
                    Log.e("MAJD", "onResponse");
                    Message msg = new Message();
                    msg.arg1 = 1;
                    msg.obj = response;
                    handler.sendMessage(msg);
                },
                error -> {
                    Log.e("MAJD", "VolleyError = " + error.getMessage());
                    if (User.getUser(context) != null && getErrorCode(error) == 700 || getErrorCode(error) == 401)
                        refreshToken(context, function);
                    else
                        handler.sendMessage(getError(error));
                }) {

            @Override
            public Map<String, String> getHeaders() {
                Log.e("MAJD", "getHeaders");

                Map<String, String> params = new HashMap<>();
                params.put("api-version", BuildConfig.VERSION_NAME);
                if (User.getUser(context) != null)
                    params.put("Authorization", "bearer " + User.getUser(context).token);
                return params;
            }
        };
        addToQueue(getRequest, "requestPin");
    }

    public static void validatePin(final Context context, String code, String number, String pin, final Callable<Void> function, final Handler handler) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("countryDialCode", code);
            jsonObject.put("mobile", number);
            jsonObject.put("pin", pin);
        } catch (Exception ignored) {
        }
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.POST, Constants.SERVER_URL + ServerUrl.VALIDATE_PIN.toString(), jsonObject,
                response -> {
                    Message msg = new Message();
                    msg.arg1 = 1;
                    msg.obj = response;
                    handler.sendMessage(msg);
                },
                error -> {
                    if (User.getUser(context) != null && getErrorCode(error) == 700 || getErrorCode(error) == 401)
                        refreshToken(context, function);
                    else
                        handler.sendMessage(getError(error));
                }) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("api-version", BuildConfig.VERSION_NAME);
                if (User.getUser(context) != null)
                    params.put("Authorization", "bearer " + User.getUser(context).token);
                return params;
            }
        };
        addToQueue(getRequest, "validatePin");
    }

    public static void register(Context context, String countryDialCode, String number, String firstName, String lastName,
                                String password, String email, String cardNumber, String cardHolderName, String expiryMonth,
                                String expiryYear, String type, String code, String model, String year, String color, final Handler handler) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("firstName", firstName);
            jsonObject.put("lastName", lastName);
            jsonObject.put("password", password);
            jsonObject.put("countryDialCode", countryDialCode);
            jsonObject.put("mobile", number);
            jsonObject.put("email", email);
            jsonObject.put("cardNumber", "");
            jsonObject.put("cardHolderName", "");
            jsonObject.put("birthday", 0);
            jsonObject.put("expiryMonth", "");
            jsonObject.put("expiryYear", "");
            jsonObject.put("type", "CARD");
            jsonObject.put("code", code);
            jsonObject.put("model", model);
            jsonObject.put("year", year);
            jsonObject.put("color", color);
        } catch (Exception ignored) {
        }
        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, Constants.SERVER_URL + ServerUrl.REGISTER.toString(), jsonObject,
                response -> {
                    Log.e("MAJD", "register onResponse");

                    Message msg = new Message();
                    msg.arg1 = 1;
                    msg.obj = response;
                    handler.sendMessage(msg);
                },
                error -> {
                    Log.e("MAJD", "register onErrorResponse =" + error.toString());

                    handler.sendMessage(getError(error));
                }) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("api-version", BuildConfig.VERSION_NAME);
                return params;
            }
        };
        addToQueue(postRequest, "register");
    }

    public static void signIn(Context context, String code, String number, String password, final Handler handler) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("mobile", number);
//            jsonObject.put("rememberClient", true);
            jsonObject.put("countryDialCode", code);
            jsonObject.put("password", password);
        } catch (Exception ignored) {
        }
        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, Constants.SERVER_URL + ServerUrl.SIGN_IN.toString(), jsonObject,
                response -> {
                    Message msg = new Message();
                    msg.arg1 = 1;
                    msg.obj = response;
                    handler.sendMessage(msg);
                },
                error -> handler.sendMessage(getError(error))) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("api-version", BuildConfig.VERSION_NAME);
                return params;
            }
        };
        addToQueue(postRequest, "signIn");
    }

    public static void resetPassword(Context context, String code, String number, String password, String pin, final Handler handler) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("countryDialCode", code);
            jsonObject.put("mobile", number);
            jsonObject.put("newPassword", password);
            jsonObject.put("pin", pin);
        } catch (Exception ignored) {
        }
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.POST, Constants.SERVER_URL + ServerUrl.RESET_PASSWORD, jsonObject,
                response -> {
                    Message msg = new Message();
                    msg.arg1 = 1;
                    msg.obj = response;
                    handler.sendMessage(msg);
                },
                error -> handler.sendMessage(getError(error))) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("api-version", BuildConfig.VERSION_NAME);
                return params;
            }
        };
        addToQueue(getRequest, "resetPassword");
    }

    public static void validateAccount(final Context context, String code, String number, String password, final Callable<Void> function, final Handler handler) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("countryDialCode", code);
            jsonObject.put("mobile", number);
            jsonObject.put("password", password);
        } catch (Exception ignored) {
        }
        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, Constants.SERVER_URL + ServerUrl.VALIDATE_ACCOUNT.toString(), jsonObject,
                response -> {
                    Message msg = new Message();
                    msg.arg1 = 1;
                    msg.obj = response;
                    handler.sendMessage(msg);
                },
                error -> handleErrorRequest(context, error, function, handler)) {

            @Override
            public Map<String, String> getHeaders() {
                return getDefaultHeader(context);
            }
        };
        addToQueue(postRequest, "validateAccount");
    }

    public static void updateMobile(final Context context, String code, String number, String pin, final Callable<Void> function, final Handler handler) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("countryDialCode", code);
            jsonObject.put("newMobile", number);
            jsonObject.put("pin", pin);
        } catch (Exception ignored) {
        }
        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, Constants.SERVER_URL + ServerUrl.UPDATE_MOBILE.toString(), jsonObject,
                response -> {
                    Message msg = new Message();
                    msg.arg1 = 1;
                    msg.obj = response;
                    handler.sendMessage(msg);
                },
                error -> handleErrorRequest(context, error, function, handler)) {

            @Override
            public Map<String, String> getHeaders() {
                return getDefaultHeader(context);
            }
        };
        addToQueue(postRequest, "updateMobile");
    }

    public static void updatePassword(final Context context, String oldPassword, String newPassword, final Callable<Void> function, final Handler handler) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("oldPassword", oldPassword);
            jsonObject.put("newPassword", newPassword);
        } catch (Exception ignored) {
        }
        JsonObjectRequest putRequest = new JsonObjectRequest(Request.Method.PUT, Constants.SERVER_URL + ServerUrl.UPDATE_PASSWORD.toString(), jsonObject,
                response -> {
                    Message msg = new Message();
                    msg.arg1 = 1;
                    msg.obj = response;
                    handler.sendMessage(msg);
                },
                error -> handleErrorRequest(context, error, function, handler)) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("api-version", BuildConfig.VERSION_NAME);
                params.put("Authorization", "bearer " + User.getUser(context).token);
                return params;
            }
        };
        addToQueue(putRequest, "updatePassword");
    }

    public static void updateProfile(final Context context, String firstName, String lastName, String email,
                                     String profilePic, final Callable<Void> function, final Handler handler) {
        JSONObject jsonObject = new JSONObject();
        try {
            String currency = "USD";
            if (User.getUser(context).currency != null) {
                currency = User.getUser(context).currency;
            }
            jsonObject.put("firstName", firstName);
            jsonObject.put("lastName", lastName);
            jsonObject.put("email", email);
            jsonObject.put("profilePic", profilePic);
            jsonObject.put("currency", currency);
        } catch (Exception ignored) {
        }
        JsonObjectRequest putRequest = new JsonObjectRequest(Request.Method.PUT, Constants.SERVER_URL + ServerUrl.UPDATE_PROFILE.toString(), jsonObject,
                response -> {
                    Message msg = new Message();
                    msg.arg1 = 1;
                    msg.obj = response;
                    handler.sendMessage(msg);
                },
                error -> handleErrorRequest(context, error, function, handler)) {

            @Override
            public Map<String, String> getHeaders() {
                return getDefaultHeader(context);
            }
        };
        addToQueue(putRequest, "updateProfile");
    }

    public static void insertCard(final Context context, String cardNumber, String cardHolderName, String expiryDate,
                                  String CVV, final Callable<Void> function, final Handler handler) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("cardNumber", cardNumber);
            jsonObject.put("cardHolderName", cardHolderName);
            jsonObject.put("expiryDate", expiryDate);
            jsonObject.put("CVV", CVV);
        } catch (Exception ignored) {
        }

        JsonObjectRequest putRequest = new JsonObjectRequest(Request.Method.PUT, Constants.SERVER_URL + "insertCard", jsonObject,
                response -> {
                    Message msg = new Message();
                    msg.arg1 = 1;
                    msg.obj = response;
                    handler.sendMessage(msg);
                },
                error -> handleErrorRequest(context, error, function, handler)) {

            @Override
            public Map<String, String> getHeaders() {
                return getDefaultHeader(context);
            }
        };
        addToQueue(putRequest, "insertCard");
    }

    public static void addCard(final Context context,
                               String cardHolderName, String cardNumber, String expirationMonth, String expirationYear, String CVV, String postalCode, String type,
                               final Callable<Void> function, final Handler handler) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("cardNumber", cardNumber);
            jsonObject.put("cardHolderName", cardHolderName);
            jsonObject.put("expiryMonth", expirationMonth);
            jsonObject.put("expiryYear", expirationYear);
            jsonObject.put("type", type);
            //Not added in the api yet
//            jsonObject.put("CVV", CVV);
//            jsonObject.put("PostalCode", postalCode);
        } catch (Exception ignored) {
        }
        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, Constants.SERVER_URL + ServerUrl.INSERT_CARD.toString(), jsonObject,
                response -> {
                    Message msg = new Message();
                    msg.arg1 = 1;
                    msg.obj = response;
                    handler.sendMessage(msg);
                },
                error -> handleErrorRequest(context, error, function, handler)) {

            @Override
            public Map<String, String> getHeaders() {
                return getDefaultHeader(context);
            }
        };
        addToQueue(postRequest, "addCard");
    }

    public static void deleteCard(final Context context, int cardId, final Callable<Void> function, final Handler handler) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("cardId", cardId);
        } catch (Exception ignored) {
        }
        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.DELETE, Constants.SERVER_URL + ServerUrl.DELETE_CARD.toString() + cardId, jsonObject,
                response -> {
                    Message msg = new Message();
                    msg.arg1 = 1;
                    msg.obj = response;
                    handler.sendMessage(msg);
                },
                error -> handleErrorRequest(context, error, function, handler)) {

            @Override
            public Map<String, String> getHeaders() {
                return getDefaultHeader(context);
            }
        };
        addToQueue(postRequest, "deleteCard");
    }

    public static void addCar(final Context context,
                              String code, String model, String year, String color,
                              final Callable<Void> function, final Handler handler) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("code", code);
            jsonObject.put("model", model);
            jsonObject.put("year", year);
            jsonObject.put("color", color);
        } catch (Exception ignored) {
        }
        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, Constants.SERVER_URL + ServerUrl.ADD_PLATE.toString(), jsonObject,
                response -> {
                    Message msg = new Message();
                    msg.arg1 = 1;
                    msg.obj = response;
                    handler.sendMessage(msg);
                },
                error -> handleErrorRequest(context, error, function, handler)) {

            @Override
            public Map<String, String> getHeaders() {
                return getDefaultHeader(context);
            }
        };
        addToQueue(postRequest, "addLicensePlate");
    }

    public static void editCar(final Context context,
                               String id, String code, String model, String year, String color,
                               final Callable<Void> function, final Handler handler) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", id);
            jsonObject.put("code", code);
            jsonObject.put("model", model);
            jsonObject.put("year", year);
            jsonObject.put("color", color);
        } catch (Exception ignored) {
        }
        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, Constants.SERVER_URL + ServerUrl.EDIT_PLATE.toString(), jsonObject,
                response -> {
                    Message msg = new Message();
                    msg.arg1 = 1;
                    msg.obj = response;
                    handler.sendMessage(msg);
                },
                error -> handleErrorRequest(context, error, function, handler)) {

            @Override
            public Map<String, String> getHeaders() {
                return getDefaultHeader(context);
            }
        };
        addToQueue(postRequest, "editLicensePlate");
    }

    public static void removeCar(final Context context,
                                 String id,
                                 final Callable<Void> function, final Handler handler) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", id);
        } catch (Exception ignored) {
        }
        JsonObjectRequest postRquest = new JsonObjectRequest(Request.Method.POST, Constants.SERVER_URL + ServerUrl.REMOVE_PLATE.toString(), jsonObject,
                response -> {
                    Message msg = new Message();
                    msg.arg1 = 1;
                    msg.obj = response;
                    handler.sendMessage(msg);
                },
                error -> handleErrorRequest(context, error, function, handler)) {

            @Override
            public Map<String, String> getHeaders() {
                return getDefaultHeader(context);
            }
        };
        addToQueue(postRquest, "removeLicensePlate");
    }

    public static void getUserPlates(final Context context, final Callable<Void> function, final Handler handler) {
        final JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, Constants.SERVER_URL + ServerUrl.GET_PLATES.toString(), null,
                response -> {
                    Message msg = new Message();
                    msg.arg1 = 1;
                    msg.obj = response;
                    handler.sendMessage(msg);
                },
                error -> {
                    if (User.getUser(context) != null && getErrorCode(error) == 700 || getErrorCode(error) == 401)
                        refreshToken(context, function);
                    else
                        handler.sendMessage(getError(error));
                }) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("api-version", BuildConfig.VERSION_NAME);
                params.put("Authorization", "bearer " + User.getUser(context).token);
                return params;
            }
        };
        addToQueue(getRequest, "getUserPlates");
    }

    public static void addFavoriteSite(final Context context, String pkeyGuidSite, final Callable<Void> function, final Handler handler) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("pkeyGuidSite", pkeyGuidSite);
        } catch (Exception ignored) {
        }
        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, Constants.SERVER_URL + "sites/addFavorite", jsonObject,
                response -> {
                    Message msg = new Message();
                    msg.arg1 = 1;
                    msg.obj = response;
                    handler.sendMessage(msg);
                },
                error -> handleErrorRequest(context, error, function, handler)) {

            @Override
            public Map<String, String> getHeaders() {
                return getDefaultHeader(context);
            }
        };
        addToQueue(postRequest, "addFavoriteSite");
    }

    public static void removeFavoriteSite(final Context context, String pkeyGuidSite, final Callable<Void> function, final Handler handler) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("pkeyGuidSite", pkeyGuidSite);
        } catch (Exception ignored) {
        }
        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, Constants.SERVER_URL + "sites/removeFavorite", jsonObject,
                response -> {
                    Message msg = new Message();
                    msg.arg1 = 1;
                    msg.obj = response;
                    handler.sendMessage(msg);
                },
                error -> handleErrorRequest(context, error, function, handler)) {

            @Override
            public Map<String, String> getHeaders() {
                return getDefaultHeader(context);
            }
        };
        addToQueue(postRequest, "removeFavoriteSite");
    }


    public static void getFavoriteSites(final Context context, final Callable<Void> function, final Handler handler) {
        final JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, Constants.SERVER_URL + "sites/favorites", null,
                response -> {
                    Message msg = new Message();
                    msg.arg1 = 1;
                    msg.obj = response;
                    handler.sendMessage(msg);
                },
                error -> {
                    if (User.getUser(context) != null && getErrorCode(error) == 700 || getErrorCode(error) == 401)
                        refreshToken(context, function);
                    else
                        handler.sendMessage(getError(error));
                }) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("api-version", BuildConfig.VERSION_NAME);
                params.put("Authorization", "bearer " + User.getUser(context).token);
                return params;
            }
        };
        addToQueue(getRequest, "getFavoriteSites");
    }

    public static void getUser(final Context context, final Callable<Void> function, final Handler handler) {
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, Constants.SERVER_URL + ServerUrl.GET_USER.toString(), null,
                response -> {
                    Log.e("MAJD", "getUser onResponse");

                    Message msg = new Message();
                    msg.arg1 = 1;
                    msg.obj = response;
                    handler.sendMessage(msg);
                },
                error -> {
                    Log.e("MAJD", "getUser onErrorResponse");

                    handleErrorRequest(context, error, function, handler);
                }) {

            @Override
            public Map<String, String> getHeaders() {
                return getDefaultHeader(context);
            }
        };
        addToQueue(getRequest, "getUser");
    }

    public static void getUser(final Context context, final Callable<Void> function) {
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, Constants.SERVER_URL + ServerUrl.GET_USER.toString(), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                if (response.has("firstName")) {
                    if (!response.has("token")) {
                        try {
                            response.put("token", User.getUser(context).token);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(TAG, "Error adding Token to user in GetUser");
                        }
                    }
                    User.setUser(context, response.toString());
                }
            }
        }, error -> {
            if ((User.getUser(context) != null) && getErrorCode(error) == 700 || getErrorCode(error) == 401) {
                refreshToken(context, function);
            } else {
                Log.e(TAG, "Error while getting user info " + getErrorCode(error));
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                return getDefaultHeader(context);
            }
        };

        addToQueue(getRequest, "getUser");
    }

    public static void rateSite(final Context context, int siteId, String rating, final Callable<Void> function, final Handler handler) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("siteId", siteId);
            jsonObject.put("rating", rating);
        } catch (Exception ignored) {
        }
        final JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, Constants.SERVER_URL + ServerUrl.POST_RATE_SITES.toString(), jsonObject,
                response -> {
                    Message msg = new Message();
                    msg.arg1 = 1;
                    msg.obj = response;
                    handler.sendMessage(msg);
                },
                error -> handleErrorRequest(context, error, function, handler)) {

            @Override
            public Map<String, String> getHeaders() {
                return getDefaultHeader(context);
            }
        };
        addToQueue(postRequest, "rateSite");
    }

    public static void getSites(final Context context, final Callable<Void> function, final Handler handler) {
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, Constants.SERVER_URL + ServerUrl.GET_SITE, null,
                response -> {
                    Message msg = new Message();
                    msg.arg1 = 1;
                    msg.obj = response;
                    handler.sendMessage(msg);
                },
                error -> handleErrorRequest(context, error, function, handler)) {

            @Override
            public Map<String, String> getHeaders() {
                return getDefaultHeader(context);
            }
        };
        addToQueue(getRequest, "getSites");
    }

    public static void siteDetails(final Context context, final int siteId, final Callable<Void> function, final Handler handler) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("siteId", siteId);
        } catch (Exception ex) {
            Log.e(TAG, "Site Details: " + ex.getLocalizedMessage());
        }
        final JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, Constants.SERVER_URL + ServerUrl.GET_SITE_DETAILS.toString() + "/" + siteId, null,
                response -> {
                    Message msg = new Message();
                    msg.arg1 = 1;
                    msg.obj = response;
                    handler.sendMessage(msg);
                },
                error -> handleErrorRequest(context, error, function, handler)) {

            @Override
            public Map<String, String> getHeaders() {
                return getDefaultHeader(context);
            }

            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<>();
                params.put("siteId", String.valueOf(siteId));
                return params;
            }
        };
        addToQueue(getRequest, "siteDetails");
    }

    public static void checkSiteAvailability(final Context context, final int siteId, final String serviceId, final long fromDate,
                                             final long toDate, final Callable<Void> function, final Handler handler) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("siteId", siteId);
            jsonObject.put("serviceId", Integer.parseInt(serviceId));
            jsonObject.put("fromDate", fromDate);
            jsonObject.put("toDate", toDate);
        } catch (Exception ex) {
            Log.e("Error Re", ex.getLocalizedMessage());
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Constants.SERVER_URL + ServerUrl.CHECK_AVAILABILITY.toString(),
                jsonObject, response -> {
            Message msg = new Message();
            msg.arg1 = 1;
            msg.obj = response;
            handler.sendMessage(msg);
        }, error -> handleErrorRequest(context, error, function, handler)) {
            @Override
            public Map<String, String> getHeaders() {
                return getDefaultHeader(context);
            }

        };

        addToQueue(request, "checkAvailabilty");
    }

    public static void bookSite(final Context context, int siteId, String serviceId, long fromDate, long toDate,
                                final Callable<Void> function, final Handler handler) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("siteId", siteId);
            jsonObject.put("serviceId", Integer.parseInt(serviceId));
            jsonObject.put("fromDate", fromDate);
            jsonObject.put("toDate", toDate);
            jsonObject.put("deviceId", "9");
        } catch (Exception ignored) {
        }
        final JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, Constants.SERVER_URL + ServerUrl.RESERVATION.toString(), jsonObject,
                response -> {
                    Message msg = new Message();
                    msg.arg1 = 1;
                    msg.obj = response;
                    handler.sendMessage(msg);
                },
                error -> handleErrorRequest(context, error, function, handler)) {

            @Override
            public Map<String, String> getHeaders() {
                return getDefaultHeader(context);
            }
        };
        addToQueue(postRequest, "bookSite");
    }

    public static void getHistory(final Context context, int index, final Callable<Void> function, final Handler handler) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Index", index);
        } catch (Exception ignored) {
        }

        final JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, Constants.SERVER_URL + ServerUrl.GET_HISTORY.toString() + index, null,
                response -> {
                    Message msg = new Message();
                    msg.arg1 = 1;
                    msg.obj = response;
                    handler.sendMessage(msg);
                },
                error -> handleErrorRequest(context, error, function, handler)) {

            @Override
            public Map<String, String> getHeaders() {
                return getDefaultHeader(context);
            }
        };
        addToQueue(getRequest, "getHistory");
    }

    public static void getCards(final Context context, final Callable<Void> function, final Handler handler) {
        final JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, Constants.SERVER_URL + ServerUrl.GET_CARD.toString(), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Message msg = new Message();
                msg.arg1 = 1;
                msg.obj = response;
                handler.sendMessage(msg);
            }
        }, error -> {
            if (User.getUser(context) != null && getErrorCode(error) == 700 || getErrorCode(error) == 401) {
                refreshToken(context, function);
            } else {
                handler.sendMessage(getError(error));
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("api-version", BuildConfig.VERSION_NAME);
                params.put("Authorization", "bearer " + User.getUser(context).token);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(getRequest);
    }

    public static void getWalletBalance(final Context context, final Callable<Void> function, final Handler handler) {
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, Constants.SERVER_URL + ServerUrl.WALLET_BALANCE.toString(), null,
                response -> {
                    Message msg = new Message();
                    msg.arg1 = 1;
                    msg.obj = response;
                    handler.sendMessage(msg);
                },
                error -> handleErrorRequest(context, error, function, handler)) {

            @Override
            public Map<String, String> getHeaders() {
                return getDefaultHeader(context);
            }
        };
        addToQueue(getRequest, "getWalletBalance");
    }

    public static void fillWallet(final Context context, double amount, int cardId, final Callable<Void> function, final Handler handler) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("amount", amount);
            jsonObject.put("cardId", cardId);
        } catch (Exception ignored) {
        }
        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, Constants.SERVER_URL + ServerUrl.FILL_WALLET.toString(), jsonObject,
                response -> {
                    Message msg = new Message();
                    msg.arg1 = 1;
                    msg.obj = response;
                    handler.sendMessage(msg);
                },
                error -> handleErrorRequest(context, error, function, handler)) {

            @Override
            public Map<String, String> getHeaders() {
                return getDefaultHeader(context);
            }
        };
        addToQueue(postRequest, "fillWallet");
    }

    public static void getWalletHistory(final Context context, int index, final Callable<Void> function, final Handler handler) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Index", index);
        } catch (Exception ignored) {
        }

        final JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, Constants.SERVER_URL + ServerUrl.WALLET_HISTORY.toString() + index, null,
                response -> {
                    Message msg = new Message();
                    msg.arg1 = 1;
                    msg.obj = response;
                    handler.sendMessage(msg);
                },
                error -> handleErrorRequest(context, error, function, handler)) {

            @Override
            public Map<String, String> getHeaders() {
                return getDefaultHeader(context);
            }
        };
        addToQueue(getRequest, "getWalletHistory");
    }

    public static void chooseDefaultPayment(final Context context, int defaultPayment, final Callable<Void> function, final Handler handler) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("defaultPaymentMethod", defaultPayment);
        } catch (Exception ignored) {
        }
        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, Constants.SERVER_URL + ServerUrl.CHOOSE_DEFAULT_PAYMENT.toString(), jsonObject,
                response -> {
                    Message msg = new Message();
                    msg.arg1 = 1;
                    msg.obj = response;
                    handler.sendMessage(msg);
                },
                error -> handleErrorRequest(context, error, function, handler)) {

            @Override
            public Map<String, String> getHeaders() {
                return getDefaultHeader(context);
            }
        };
        addToQueue(postRequest, "fillWallet");
    }

    public static void aboutUs(final Context context, final Callable<Void> function, final Handler handler) {
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, Constants.SERVER_URL + "aboutUs", null,
                response -> {
                    Message msg = new Message();
                    msg.arg1 = 1;
                    msg.obj = response;
                    handler.sendMessage(msg);
                },
                error -> {
                    if (User.getUser(context) != null && getErrorCode(error) == 700 || getErrorCode(error) == 401)
                        refreshToken(context, function);
                    else
                        handler.sendMessage(getError(error));
                }) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("api-version", BuildConfig.VERSION_NAME);
                params.put("Authorization", "bearer " + User.getUser(context).token);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(getRequest);
    }

    public static void help(final Context context, final Callable<Void> function, final Handler handler) {
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, Constants.SERVER_URL + "help", null,
                response -> {
                    Message msg = new Message();
                    msg.arg1 = 1;
                    msg.obj = response;
                    handler.sendMessage(msg);
                },
                error -> {
                    if (User.getUser(context) != null && getErrorCode(error) == 700 || getErrorCode(error) == 401)
                        refreshToken(context, function);
                    else
                        handler.sendMessage(getError(error));
                }) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("api-version", BuildConfig.VERSION_NAME);
                params.put("Authorization", "bearer " + User.getUser(context).token);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(getRequest);
    }

    public static void setDefaultPayment(final Context context, int id, final Callable<Void> function, final Handler handler) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("defaultPaymentMethod", id);
        } catch (Exception ex) {
            Log.e(TAG, ex.getLocalizedMessage());
        }
        JsonRequest postRequest = new JsonObjectRequest(Request.Method.POST, Constants.SERVER_URL + ServerUrl.SET_DEFAULT_CARD.toString(), jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Message msg = new Message();
                msg.arg1 = 1;
                msg.obj = response;
                handler.sendMessage(msg);
            }
        }, error -> handleErrorRequest(context, error, function, handler)) {
            @Override
            public Map<String, String> getHeaders() {
                return getDefaultHeader(context);
            }
        };

        addToQueue(postRequest, "setDefaultPayment");
    }

    enum ServerUrl {

        /**
         * ACTION
         */
        RESERVATION("Action/reservation"),
        SUBSCRIBE_MEMBER("Action/SubscribeMember"),
        ADD_PLATE("Subscriber/AddCar"),
        EDIT_PLATE("Subscriber/EditCar"),
        REMOVE_PLATE("Subscriber/RemoveCar"),
        GET_PLATES("Subscriber/GetCars"),
        CHECK_PLATE("Action/CheckLP"),
        CANCEL_RESERVATION("Action/CancelReservation"),
        BOOK_WITH_BARCODE("Action/GenerateBarcode"),


        /**
         * AUTH
         */
        SIGN_IN("auth/SignIn"),
        CHECK_MOBILE("auth/CheckMobile"),
        REQUEST_PIN("auth/RequestPin"),
        VALIDATE_PIN("auth/ValidatePin"),
        REGISTER("auth/Register"),
        LOGOUT("auth/Logout"),
        RESET_PASSWORD("auth/ResetPassword"),
        UPDATE_PROFILE("auth/UpdateProfile"),
        UPDATE_PASSWORD("auth/UpdatePassword"),
        CHECK_UPDATE("auth/isDataUpdated"),
        UPDATE_MOBILE("auth/UpdateMobile"),
        GET_USER("auth/GetUser"),
        VALIDATE_ACCOUNT("auth/ValidateAccount"),

        /**
         * PAYMENT
         */
        INSERT_CARD("Cards/InsertCard"),
        VALIDATE_CARD("Cards/ValidateCard"),
        GET_CARD("Cards"),
        DELETE_CARD("Cards/"),
        GET_CARDID("Cards/"),
        SET_DEFAULT_CARD("ChooseDefaultPayment"),
        /**
         * Sites
         */
        POST_RATE_SITES("Sites/rateSite"),
        CHECK_AVAILABILITY("Sites/checkSiteAvailability"),
        GET_SITE("sites"),

        /**
         * Wallet
         */
        FILL_WALLET("wallet/fill"),
        DEDUCT_WALLET("wallet/deduct"),
        REFUND_WALLET("wallet/refund"),
        WALLET_HISTORY("wallet/history/"),
        WALLET_BALANCE("wallet/balance"),

        /**
         * OTHER
         */
        GET_HISTORY("GetTimeLine/"),
        GET_SITE_DETAILS("Sites/siteDetails"),
        CHOOSE_DEFAULT_PAYMENT("chooseDefaultPayment");


        private String subUrl;

        ServerUrl(String string) {
            this.subUrl = string;
        }

        @NotNull
        @Override
        public String toString() {
            return subUrl;
        }
    }

    private static Map<String, String> getDefaultHeader(Context context) {
        Map<String, String> header = new HashMap<>();
        header.put("api-version", BuildConfig.VERSION_NAME);
        header.put("Authorization", "bearer " + User.getUser(context).token);
        return header;
    }

    private static void handleErrorRequest(Context context, VolleyError error, Callable<Void> function, Handler handler) {
        Log.e("MAJD", "HandleErrorRequest error =" + error.getMessage());
        Log.e("MAJD", "ErrorCode = " + getErrorCode(error));

        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
            Toast.makeText(context,
                    context.getString(R.string.error_network_timeout),
                    Toast.LENGTH_LONG).show();
            try {
                function.call();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (error instanceof AuthFailureError) {
            refreshToken(context, function);
            Toast.makeText(context, "Token Expired", Toast.LENGTH_SHORT).show();
        } else if (error instanceof ServerError) {
            Message msg = new Message();
            msg.arg2 = ErrorRequest.ErrorType.ServerError.toInt();
            msg.obj = ErrorRequest.ErrorType.ServerError.toString();
            handler.sendMessage(msg);
        } else if (error instanceof NetworkError) {
            Message msg = new Message();
            msg.arg2 = ErrorRequest.ErrorType.NetworkError.toInt();
            msg.obj = ErrorRequest.ErrorType.NetworkError.toString();
            handler.sendMessage(msg);
        } else if (error instanceof ParseError) {
            Message msg = new Message();
            msg.arg2 = ErrorRequest.ErrorType.ParseError.toInt();
            msg.obj = ErrorRequest.ErrorType.ParseError.toString();
            handler.sendMessage(msg);
        } else if (getErrorCode(error) == 700 || getErrorCode(error) == 401) {
            refreshToken(context, function);
        } else {
            handler.sendMessage(getError(error));
        }

    }

    public static DefaultRetryPolicy getRetryPolicy(final int TimeOut, final int RetryCount) {
        return new DefaultRetryPolicy(TimeOut, RetryCount, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
    }

    private static void addToQueue(Request request, String tag) {
        MyApplication.getInstance().addToRequestQueue(request, tag);
    }
}