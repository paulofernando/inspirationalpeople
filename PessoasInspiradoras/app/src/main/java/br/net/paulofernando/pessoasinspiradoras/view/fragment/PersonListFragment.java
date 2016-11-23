package br.net.paulofernando.pessoasinspiradoras.view.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import br.net.paulofernando.pessoasinspiradoras.R;
import br.net.paulofernando.pessoasinspiradoras.data.dao.DatabaseHelper;
import br.net.paulofernando.pessoasinspiradoras.data.entity.Person;
import br.net.paulofernando.pessoasinspiradoras.listener.EndlessRecyclerViewScrollListener;
import br.net.paulofernando.pessoasinspiradoras.view.adapter.PersonAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;

public class PersonListFragment extends Fragment {

    private static final String TAG = "PersonListFragment";

    @BindView(R.id.list_rv) RecyclerView mRecyclerView;

    protected RecyclerView.LayoutManager mLayoutManager;
    protected PersonAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, rootView);

        mRecyclerView.setItemViewCacheSize(30);
        mRecyclerView.setDrawingCacheEnabled(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        setRecyclerViewLayoutManager();

        getData();

        return rootView;
    }

    /**
     * Set RecyclerView's LayoutManager to the one given.
     */
    public void setRecyclerViewLayoutManager() {
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        positionScroll();
        mRecyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                loadMoreData(page);
            }
        });
    }

    private void positionScroll() {
        int scrollPosition = 0;
        // If a layout manager has already been set, get current scroll position.
        if (mRecyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }
        mRecyclerView.scrollToPosition(scrollPosition);
    }


    public void loadMoreData(int page) {
        /*Log.i(TAG, "Loading page " + (page + 1) + "...");
        loadingTextView.setVisibility(View.VISIBLE);
        fillPeopleList(page + 1);*/
    }

    protected void getData() {
        fillPeopleList(1);
    }

    public void fillPeopleList(final int pageNumber) {
        DatabaseHelper helper = new DatabaseHelper(this.getContext());

        final List<Person> result = helper.getPersonsData();
        if ((result != null) && (PersonListFragment.this.getActivity() != null)) {
            PersonListFragment.this.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateList(result);
                }
            });
        }

        helper.close();
    }

    private void updateList(List<Person> result) {
        if (mAdapter == null) {
            mAdapter = new PersonAdapter(PersonListFragment.this.getContext());
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.setItems(result);
            mRecyclerView.setVisibility(View.VISIBLE);
        } else {
            mAdapter.addList(result);
        }
        //loadingTextView.setVisibility(View.GONE);
    }

}
