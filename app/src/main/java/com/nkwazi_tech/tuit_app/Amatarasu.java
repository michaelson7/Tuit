package com.nkwazi_tech.tuit_app;//
//     Toast.makeText(getContext(), error.toString(), Toast.LENGTH_LONG).show();
//TODO DIALOG
/*

todo pop menu change
 Menu menuOpts = popup.getMenu();
menuOpts.getItem(0).setTitle("Subscribe");

    imgview = new Dialog(mCtx);

  private void OpenImg(String title, String imgpath) {
        imgview.setContentView(R.layout.dialog_imgview);
        Objects.requireNonNull(imgview.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        Window window = imgview.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        TextView titles, downloadbtn;
        ImageView img;

        titles = imgview.findViewById(R.id.userpost);
        downloadbtn = imgview.findViewById(R.id.downloadbtn);
        img = imgview.findViewById(R.id.postimg);

        titles.setText(title);
        Glide.with(mCtx)
                .load(imgpath)
                .into(img);
        downloadbtn.setOnClickListener(v -> {
            String path = imgpath.replaceAll("http://nawa777.000webhostapp.com/uploads/","");
            Uri uri = Uri.parse(imgpath);
            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setTitle("img");
            request.setDescription("Downloading");
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setVisibleInDownloadsUi(false);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, path);
            DownloadManager manager = (DownloadManager) Objects.requireNonNull(mCtx).getSystemService(Context.DOWNLOAD_SERVICE);
            assert manager != null;
            manager.enqueue(request);
        });

        imgview.show();
    }*/
// TODO: set selectablecbackground
/*
          android:clickable="true"
          android:focusable="true"
          android:foreground="?attr/selectableItemBackground"
           android:background="?selectableItemBackgroundBorderless"

//TODO: Set Color/
//c3.getBackground().setColorFilter(Color.parseColor("#062C00"), PorterDuff.Mode.DARKEN);
// submit.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#00574B")));
// submit.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#00574B")));

//TODO: Sorting Algorithim. flio 01 02 for inverse
//sort   Collections.sort(dataHandlerVideoInfoList, (o1, o2) -> o1.getTitle().compareTo(o2.getTitle()));
//sorting by integer     Collections.sort(dataHandlerVideoInfoList, (o1, o2) -> o1.getViews()-o2.getViews());

Todo: sending Get Request
private void Check_Subscription(String courseid, String id) {
        String data = "&courseid=" + courseid + "&userID=" + id;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLs.check_Subscription + data,
                response -> {
                    try {
                        //converting the string to json array object
                        JSONObject obj = new JSONObject(response);

                        if (obj.getBoolean("state")) {
                            subscription.setText("Not Subscribed");
                        }
                        loadlecturer();
                        loadProducts();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(getContext(), error.toString(), Toast.LENGTH_LONG).show());

        Volley.newRequestQueue(getContext()).add(stringRequest);
    }

//TODO: Loading from database w/o string

 private void LoadGroup() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLs.URL_loadcourse,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //converting the string to json array object
                            JSONObject obj = new JSONObject(response);
                            JSONArray array = obj.getJSONArray("Products");

                            //traversing through all the object
                            for (int i = 0; i < array.length(); i++) {

                                //getting product object from json array
                                JSONObject product = array.getJSONObject(i);

                                dataHandler_group_all_lists.add(new DataHandler_Group_all_list(
                                        product.getString("groupname"),
                                        product.getString("groupimage"),
                                        product.getString("groupdescription"),
                                        product.getString("admin"),
                                        100
                                ));
                            }
                            Adapter_Group_all_list adapter = new Adapter_Group_all_list(getContext(), dataHandler_group_all_lists);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                            recyclerView.setAdapter(adapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), error.toString(), Toast.LENGTH_LONG).show();
                    }
                });
        //adding our stringrequest to queue
        Volley.newRequestQueue(getContext()).add(stringRequest);
    }

TODO: get from db with string

  private void loadProducts() {
   @SuppressLint("StaticFieldLeak")
        class UserLogin extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler requestHandler = new RequestHandler();
                HashMap<String, String> params = new HashMap<>();
                params.put("page", String.valueOf(1));
                return requestHandler.sendPostRequest(URLs.URL_getbookmarks, params);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                progressBar.setVisibility(View.GONE);
                try {
                    JSONObject obj = new JSONObject(s);
                    JSONArray array = obj.getJSONArray("Products");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject product = array.getJSONObject(i);
                        dataHandler_generalResearches.add(new DataHandler_GeneralResearch(
                                1,"","","",""
                        ));
                    }

                    Adapter_Video adapter = new Adapter_Video(getContext(), dataHandlerVideoInfoList);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    recyclerView.setAdapter(adapter);
                    progressBar.setVisibility(View.GONE);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        UserLogin ul = new UserLogin();
        ul.execute();
    }

////////////////////////////////////////////TODO sending to server and getting response
 private void LikeEvent() {
        int videoid = DataHandler_VideoPlayerInfo.getInstance(getContext()).getVideoid();
        @SuppressLint("StaticFieldLeak")
        class LikesState extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("studentid", String.valueOf(studentid));
                params.put("videoid", String.valueOf(videoid));
                params.put("likenum", String.valueOf(likesnum));

                //returing the response
                return requestHandler.sendPostRequest(URLs.URL_setLikes, params);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    //converting response to json object
                    JSONObject obj = new JSONObject(s);
                    //if no error in response
                    if (!obj.getBoolean("error")) {
                        Toast.makeText(getContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        LikesState ul = new LikesState();
        ul.execute();
    }

    todo fragmentmanager
      FragmentManager manager = ((AppCompatActivity) mCtx).getSupportFragmentManager();
            FragmentTransaction fr = manager.beginTransaction();
            fr.replace(R.id.flContent, new PDFviewer_Frag());
            fr.addToBackStack(null);
            fr.commit();



////////////////////////////////////////////TODO firebase
  GroupNameRef = FirebaseDatabase.getInstance().getReference("Groups").child(GroupName);;
        GroupNameRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mChat.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    DataHandler_Chat chat = snapshot.getValue(DataHandler_Chat.class);
                    mChat.add(chat);

                    adapter_chat = new Adapter_Chat(GroupChat_activity.this, mChat, "nazier");
                    recyclerView.setAdapter(adapter_chat);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
*/

