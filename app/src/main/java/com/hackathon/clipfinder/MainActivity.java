package com.hackathon.clipfinder;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.hackathon.clipfinder.adapters.ClipPlaylistsAdapter;
import com.hackathon.clipfinder.listeners.RecyclerTouchListener;
import com.hackathon.clipfinder.models.ClipPlaylist;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ClipPlaylistsAdapter adapter;
    private List<ClipPlaylist> albumList;

    SharedPreferences sharedPrefs;
    Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initCollapsingToolbar();

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        albumList = new ArrayList<>();
        adapter = new ClipPlaylistsAdapter(this, albumList);

        sharedPrefs = getApplication().getSharedPreferences("Playlists", Context.MODE_PRIVATE);



        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(MainActivity.this, PlayerActivity.class);
                ClipPlaylist clipPlaylist = albumList.get(position);

                Bundle bundle = new Bundle();
                bundle.putSerializable("playlist", clipPlaylist);
                intent.putExtras(bundle);
                startActivity(intent);

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        prepareAlbums();

        try {
            Glide.with(this).load(R.drawable.cover).into((ImageView) findViewById(R.id.backdrop));
        } catch (Exception e) {
            e.printStackTrace();
        }

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CreatePlaylistActivity.class);
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        albumList.clear();
        prepareAlbums();
        adapter.notifyDataSetChanged();
    }


        /**
         * Initializing collapsing toolbar
         * Will show and hide the toolbar title on scroll
         */
    private void initCollapsingToolbar() {
        final CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(" ");
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);

        // hiding & showing the title when toolbar expanded & collapsed
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setTitle(getString(R.string.app_name));
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbar.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }

    /**
     * Adding few albums for testing
     */
    private void prepareAlbums() {
        int[] covers = new int[]{
                R.drawable.album1,
                R.drawable.album2,
                R.drawable.album3,
                R.drawable.album4,
                R.drawable.album5,
                R.drawable.album6,
                R.drawable.album7,
                R.drawable.album8,
                R.drawable.album9,
                R.drawable.album10,
                R.drawable.album11};



        Map<String, ?> prefs = sharedPrefs.getAll();
        Log.i("Playlists", "Playlists "+ prefs);
        for(Object value: prefs.values()){
            String playlistString = (String) value;
            Log.i("String", "String Playlist "+ playlistString );
            ClipPlaylist clipPlaylist = gson.fromJson(playlistString, ClipPlaylist.class);
            if(clipPlaylist != null && clipPlaylist.getSceneMetadataList().size() > 0) {
                albumList.add(clipPlaylist);
            }
        }
        /*
        ClipPlaylist a = new ClipPlaylist("Romance", 13, covers[0]);
        albumList.add(a);

        a = new ClipPlaylist("Xscpae", 8, covers[1]);
        albumList.add(a);



        a = new ClipPlaylist("Maroon 5", 11, covers[2]);
        albumList.add(a);

        a = new ClipPlaylist("Born to Die", 12, covers[3]);
        albumList.add(a);

        a = new ClipPlaylist("Honeymoon", 14, covers[4]);
        albumList.add(a);

        a = new ClipPlaylist("I Need a Doctor", 1, covers[5]);
        albumList.add(a);

        a = new ClipPlaylist("Loud", 11, covers[6]);
        albumList.add(a);

        a = new ClipPlaylist("Legend", 14, covers[7]);
        albumList.add(a);

        a = new ClipPlaylist("Hello", 11, covers[8]);
        albumList.add(a);

        a = new ClipPlaylist("Greatest Hits", 17, covers[9]);
        albumList.add(a);

        adapter.notifyDataSetChanged();
        */
    }

    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }
}
