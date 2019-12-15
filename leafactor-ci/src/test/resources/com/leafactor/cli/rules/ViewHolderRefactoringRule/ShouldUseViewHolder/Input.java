package test.resources.com.leafactor.cli.rules.ViewHolderRefactoringRule.ShouldUseViewHolder;

public abstract class Input extends BaseAdapter {
    public static class Adapter2 extends ViewHolderSample {
        LayoutInflater mInflater;

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = convertView != null ? convertView : mInflater.inflate(R.layout.your_layout, null);

            TextView text = (TextView) convertView.findViewById(R.id.text);
            text.setText("Position " + position);

            return convertView;
        }
    }

    public static class Adapter3 extends ViewHolderSample {
        LayoutInflater mInflater;

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = convertView != null ? convertView : mInflater.inflate(R.layout.your_layout, null);
            TextView text1 = (TextView) convertView.findViewById(R.id.text1);
            TextView text2 = (TextView) convertView.findViewById(R.id.text2);
            TextView text3 = (TextView) convertView.findViewById(R.id.text3);
            TextView text4 = (TextView) convertView.findViewById(R.id.text4);
            TextView text5 = (TextView) convertView.findViewById(R.id.text5);
            TextView text6 = (TextView) convertView.findViewById(R.id.text6);
            text1.setText("Position " + position);
            return convertView;
        }
    }
}