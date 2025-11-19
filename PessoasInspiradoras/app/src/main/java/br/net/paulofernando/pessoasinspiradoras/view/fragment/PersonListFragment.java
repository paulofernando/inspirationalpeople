package br.net.paulofernando.pessoasinspiradoras.view.fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import br.net.paulofernando.pessoasinspiradoras.data.dao.DatabaseHelper;
import br.net.paulofernando.pessoasinspiradoras.data.entity.Person;
import br.net.paulofernando.pessoasinspiradoras.databinding.FragmentMainBinding;
import br.net.paulofernando.pessoasinspiradoras.listener.EndlessRecyclerViewScrollListener;
import br.net.paulofernando.pessoasinspiradoras.view.adapter.PersonAdapter;

public class PersonListFragment extends Fragment {

    private static final String TAG = "PersonListFragment";
    public static boolean UPDATE_PERSON_LIST = false;

    private FragmentMainBinding binding;

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
        binding = FragmentMainBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();

        binding.listRv.setItemViewCacheSize(30);
        binding.listRv.setDrawingCacheEnabled(true);
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
        binding.listRv.setLayoutManager(mLayoutManager);
        positionScroll();
        binding.listRv.addOnScrollListener(new EndlessRecyclerViewScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                loadMoreData(page);
            }
        });
    }

    private void positionScroll() {
        int scrollPosition = 0;
        // If a layout manager has already been set, get current scroll position.
        if (binding.listRv.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) binding.listRv.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }
        binding.listRv.scrollToPosition(scrollPosition);
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
            binding.listRv.setAdapter(mAdapter);
            mAdapter.setItems(result);
            binding.listRv.setVisibility(View.VISIBLE);
        } else {
            mAdapter.addList(result);
        }

        if(result.size() > 0) {
            binding.noInspiration.setVisibility(View.GONE);
        } else {
            binding.noInspiration.setVisibility(View.VISIBLE);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
