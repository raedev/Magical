package com.zhihu.matisse.internal.ui;

import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.ColorUtils;
import androidx.fragment.app.Fragment;

import com.zhihu.matisse.R;
import com.zhihu.matisse.internal.entity.Item;
import com.zhihu.matisse.internal.entity.SelectionSpec;
import com.zhihu.matisse.internal.ui.widget.MagicalImageView;
import com.zhihu.matisse.internal.utils.PhotoMetadataUtils;

import it.sephiroth.android.library.imagezoom.ImageViewTouchBase;

public class PreviewImageFragment extends Fragment {

    private static final String ARGS_ITEM = "args_item";
    private static final String ARGS_ATTACH_TRANSITION_NAME = "ARGS_ATTACH_TRANSITION_NAME";
    private ViewGroup mContainerLayout;

    public static PreviewImageFragment newInstance(Item item, boolean attachTransitionName) {
        PreviewImageFragment fragment = new PreviewImageFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARGS_ITEM, item);
        bundle.putBoolean(ARGS_ATTACH_TRANSITION_NAME, attachTransitionName);
        fragment.setArguments(bundle);
        return fragment;
    }

    private MagicalImageView mMagicalImageView;
    private Item mCurrentItem;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContainerLayout = (ViewGroup) container.getParent();
        return inflater.inflate(R.layout.fragment_preview_image_item, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() == null) return;
        final Item item = getArguments().getParcelable(ARGS_ITEM);
        final boolean attachable = getArguments().getBoolean(ARGS_ATTACH_TRANSITION_NAME);
        if (item == null) {
            return;
        }
        mCurrentItem = item;
        mMagicalImageView = view.findViewById(R.id.image_view);
        mMagicalImageView.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
        mMagicalImageView.setOnDismissListener(new MagicalImageView.onMagicalImageViewDismissListener() {
            @Override
            public void onImageViewSliding(float ratio) {
                int hexAlpha = Math.min(255, (int) (255 * (1.0F - ratio)));
                int color = ColorUtils.setAlphaComponent(Color.BLACK, Math.max(102, hexAlpha));
                mContainerLayout.setBackgroundColor(color);
            }

            @Override
            public void onImageViewDismiss() {
                ActivityCompat.finishAfterTransition(requireActivity());
            }
        });
        if (attachable) {
            attachTransitionName();
        }
        this.loadImage();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        detachTransitionName();
    }

    void attachTransitionName() {
        if (mMagicalImageView != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mMagicalImageView.setTransitionName("preview");
        }
    }

    void detachTransitionName() {
        if (mMagicalImageView != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mMagicalImageView.setTransitionName(null);
        }
    }

    private void loadImage() {
        try {
            Point size = PhotoMetadataUtils.getBitmapSize(mCurrentItem.getContentUri(), requireActivity());
            if (mCurrentItem.isGif()) {
                SelectionSpec.getInstance().imageEngine.loadGifImage(getContext(), size.x, size.y, mMagicalImageView,
                        mCurrentItem.getContentUri());
            } else {
                SelectionSpec.getInstance().imageEngine.loadImage(getContext(), size.x, size.y, mMagicalImageView,
                        mCurrentItem.getContentUri());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
