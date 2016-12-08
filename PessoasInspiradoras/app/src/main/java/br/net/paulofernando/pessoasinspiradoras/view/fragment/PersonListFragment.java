package br.net.paulofernando.pessoasinspiradoras.view.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

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
    public static boolean UPDATE_PERSON_LIST = false;

    @BindView(R.id.list_rv) RecyclerView mRecyclerView;

    protected RecyclerView.LayoutManager mLayoutManager;
    protected PersonAdapter mAdapter;
    @BindView(R.id.no_inspiration) LinearLayout noInspiration;

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
    }

    protected void getData() {
        fillPeopleList();
    }

    public void fillPeopleList() {
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

        if(result.size() > 0) {
            noInspiration.setVisibility(View.GONE);
        } else {
            noInspiration.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Verifies if there are difference between the list and the database and update it if necessary.
     */
    public void syncList() {
        DatabaseHelper helper = new DatabaseHelper(this.getContext());
        if((mAdapter.getItemCount() != helper.getPersonsData().size()) || (UPDATE_PERSON_LIST)) {
            mAdapter = null;
            fillPeopleList();
            UPDATE_PERSON_LIST = false;
        }
        helper.close();
    }

}
