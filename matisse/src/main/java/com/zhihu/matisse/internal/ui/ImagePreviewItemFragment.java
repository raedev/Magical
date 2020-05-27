package com.zhihu.matisse.internal.ui;

import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.ColorUtils;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;

import com.zhihu.matisse.R;
import com.zhihu.matisse.internal.entity.Item;
import com.zhihu.matisse.internal.entity.SelectionSpec;
import com.zhihu.matisse.internal.ui.widget.MagicalImageView;
import com.zhihu.matisse.internal.utils.PhotoMetadataUtils;

import it.sephiroth.android.library.imagezoom.ImageViewTouchBase;

public class ImagePreviewItemFragment extends Fragment {

    private static final String ARGS_ITEM = "args_item";
    private static final String ARGS_TRANSITION_NAME = "ARGS_ATTACH_TRANSITION_NAME";
    private ViewGroup mContainerLayout;

    public static ImagePreviewItemFragment newInstance(Item item, @Nullable String transitionName) {
        ImagePreviewItemFragment fragment = new ImagePreviewItemFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARGS_ITEM, item);
        bundle.putString(ARGS_TRANSITION_NAME, transitionName);
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
        if (item == null) {
            return;
        }
        mCurrentItem = item;
        mMagicalImageView = view.findViewById(R.id.image_view);
        ViewCompat.setTransitionName(mMagicalImageView, getArguments().getString(ARGS_TRANSITION_NAME, "preview"));
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
        mMagicalImageView.setOnLongClickListener(v -> {
            SelectionSpec spec = SelectionSpec.getInstance();
            if (spec.hasInited && spec.onLongClickListener != null) {
                spec.onLongClickListener.onLongClick(ImagePreviewItemFragment.this, mMagicalImageView, item);
                return true;
            }
            return false;
        });

        this.loadImage();
    }

    public MagicalImageView getMagicalImageView() {
        return mMagicalImageView;
    }

    private void loadImage() {
        try {
            Item item = mCurrentItem;
            Point size = PhotoMetadataUtils.getBitmapSize(item.getContentUri(), requireActivity());
            if (item.isGif()) {
                SelectionSpec.getInstance().imageEngine.loadGifImage(getContext(), size.x, size.y, mMagicalImageView,
                        item.getContentUri());
            } else {
                SelectionSpec.getInstance().imageEngine.loadImage(getContext(), size.x, size.y, mMagicalImageView,
                        item.getContentUri());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
