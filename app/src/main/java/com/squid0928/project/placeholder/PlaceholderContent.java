package com.squid0928.project.placeholder;

import android.graphics.Bitmap;
import android.media.Image;

import com.squid0928.project.MapsActivity;
import com.squid0928.project.utils.UserData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class PlaceholderContent {

    /**
     * An array of sample (placeholder) items.
     */
    public static final List<PlaceholderItem> ITEMS = new ArrayList<PlaceholderItem>();

    /**
     * A map of sample (placeholder) items, by ID.
     */
    public static final Map<String, PlaceholderItem> ITEM_MAP = new HashMap<String, PlaceholderItem>();

    private static int COUNT = 0;

    static {
        // Add some sample items.
        UserData data = MapsActivity.user_data.get("phantomsquid0928"); //TODO server must give this info
        List<UserData> friends = data.getFriends();
        COUNT = friends.size();
        for (int i = 0; i < COUNT; i++) {
            UserData friend = friends.get(i);
            Bitmap image = friend.getAccountPhoto();
            String friendName = friend.getName();
            addItem(createPlaceholderItem(i, image, friendName));
        }
    }

    private static void addItem(PlaceholderItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    private static PlaceholderItem createPlaceholderItem(int position, Bitmap image, String friendName) {
        return new PlaceholderItem(image, String.valueOf(position), friendName, makeDetails(position));
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

    /**
     * A placeholder item representing a piece of content.
     */
    public static class PlaceholderItem {
        public final Bitmap userImage;
        public final String id;
        public final String content;
        public final String details;

        public PlaceholderItem(Bitmap userImage, String id, String content, String details) {
            this.userImage = userImage;
            this.id = id;
            this.content = content;
            this.details = details;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}