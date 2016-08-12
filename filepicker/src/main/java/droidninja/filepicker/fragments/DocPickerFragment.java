package droidninja.filepicker.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.android.internal.util.Predicate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import droidninja.filepicker.FilePickerConst;
import droidninja.filepicker.R;
import droidninja.filepicker.adapters.SectionsPagerAdapter;
import droidninja.filepicker.cursors.loadercallbacks.FileResultCallback;
import droidninja.filepicker.models.Document;
import droidninja.filepicker.utils.MediaStoreHelper;
import droidninja.filepicker.utils.Utils;


public class DocPickerFragment extends BaseFragment {

    private static final String TAG = DocPickerFragment.class.getSimpleName();

    public static final String PDF_FRAGMENT = "PDF";
    public static final String PPT_FRAGMENT = "PPT";
    public static final String WORD_FRAGMENT = "DOC";
    public static final String EXCEL_FRAGMENT = "XLS";
    public static final String TXT_FRAGMENT = "TXT";

    TabLayout tabLayout;

    ViewPager viewPager;
    private ArrayList<String> selectedPaths;
    private ProgressBar progressBar;

    public DocPickerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_doc_picker, container, false);
    }

    public static DocPickerFragment newInstance(ArrayList<String> selectedPaths) {
        DocPickerFragment docPickerFragment = new DocPickerFragment();
        docPickerFragment.selectedPaths = selectedPaths;
        return  docPickerFragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setViews(view);
        initView();
    }

    private void initView() {
        setUpViewPager();
        setData();
    }

    private void setViews(View view) {
        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
    }

    private void setData() {
        MediaStoreHelper.getDocs(getActivity(), new FileResultCallback<Document>() {
            @Override
            public void onResultCallback(List<Document> files) {
                progressBar.setVisibility(View.GONE);
                setDataOnFragments(files);
            }
        });
    }

    private void setDataOnFragments(List<Document> files) {
        SectionsPagerAdapter sectionsPagerAdapter = (SectionsPagerAdapter) viewPager.getAdapter();
        if(sectionsPagerAdapter!=null)
        {
            for (int index = 0; index < sectionsPagerAdapter.getCount(); index++) {
                DocFragment docFragment = (DocFragment) sectionsPagerAdapter.getItem(index);
                if(docFragment!=null)
                {
                    if(index==0)
                        docFragment.updateList(filterDocuments(FilePickerConst.FILE_TYPE.PDF, files));
                    else if(index==1)
                        docFragment.updateList(filterDocuments(FilePickerConst.FILE_TYPE.PPT, files));
                    else if(index==2)
                        docFragment.updateList(filterDocuments(FilePickerConst.FILE_TYPE.WORD, files));
                    else if(index==3)
                        docFragment.updateList(filterDocuments(FilePickerConst.FILE_TYPE.EXCEL, files));
                    else if(index==4)
                        docFragment.updateList(filterDocuments(FilePickerConst.FILE_TYPE.TXT, files));
                }
            }
        }
    }

    private void setUpViewPager() {
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getChildFragmentManager());
        adapter.addFragment(DocFragment.newInstance(selectedPaths), PDF_FRAGMENT);
        adapter.addFragment(DocFragment.newInstance(selectedPaths), PPT_FRAGMENT);
        adapter.addFragment(DocFragment.newInstance(selectedPaths), WORD_FRAGMENT);
        adapter.addFragment(DocFragment.newInstance(selectedPaths), EXCEL_FRAGMENT);
        adapter.addFragment(DocFragment.newInstance(selectedPaths), TXT_FRAGMENT);

        viewPager.setOffscreenPageLimit(5);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    private ArrayList<Document> filterDocuments(final FilePickerConst.FILE_TYPE type, List<Document> documents)
    {
        final Predicate<Document> docType = new Predicate<Document>() {
            public boolean apply(Document document) {
                return document.isThisType(type);
            }
        };

        return new ArrayList<>(Utils.filter(new HashSet<>(documents),docType));
    }
}
