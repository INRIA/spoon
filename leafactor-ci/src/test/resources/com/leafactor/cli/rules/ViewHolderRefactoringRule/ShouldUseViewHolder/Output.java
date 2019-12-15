package test.resources.com.leafactor.cli.rules.ViewHolderRefactoringRule.ShouldUseViewHolder;


import R.layout.your_layout;




public abstract class Input extends BaseAdapter {
    public static class Adapter2 extends ViewHolderSample {
        LayoutInflater mInflater;

        static class ViewHolderItem {
            public TextView text = null;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = convertView != null ? convertView : mInflater.inflate(R.layout.your_layout, null);
            ViewHolderItem viewHolderItem = (ViewHolderItem) convertView.getTag();
            if (viewHolderItem == null) {
                viewHolderItem = new ViewHolderItem();
                convertView.setTag(viewHolderItem);
                viewHolderItem.text = (TextView) convertView.findViewById(R.id.text);
            }

            TextView text = viewHolderItem.text;
            text.setText("Position " + position);

            return convertView;
        }
    }

    public static class Adapter3 extends ViewHolderSample {
        LayoutInflater mInflater;

        static class ViewHolderItem {
            public TextView text1 = null;

            public TextView text2 = null;

            public TextView text3 = null;

            public TextView text4 = null;

            public TextView text5 = null;

            public TextView text6 = null;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = convertView != null ? convertView : mInflater.inflate(R.layout.your_layout, null);
            ViewHolderItem viewHolderItem = (ViewHolderItem) convertView.getTag();
            if (viewHolderItem == null) {
                viewHolderItem = new ViewHolderItem();
                convertView.setTag(viewHolderItem);
                viewHolderItem.text1 = (TextView) convertView.findViewById(R.id.text1);
                viewHolderItem.text2 = (TextView) convertView.findViewById(R.id.text2);
                viewHolderItem.text3 = (TextView) convertView.findViewById(R.id.text3);
                viewHolderItem.text4 = (TextView) convertView.findViewById(R.id.text4);
                viewHolderItem.text5 = (TextView) convertView.findViewById(R.id.text5);
                viewHolderItem.text6 = (TextView) convertView.findViewById(R.id.text6);
            }
            TextView text1 = viewHolderItem.text1;
            TextView text2 = viewHolderItem.text2;
            TextView text3 = viewHolderItem.text3;
            TextView text4 = viewHolderItem.text4;
            TextView text5 = viewHolderItem.text5;
            TextView text6 = viewHolderItem.text6;
            text1.setText("Position " + position);
            return convertView;
        }
    }
}