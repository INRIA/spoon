package test.resources.com.leafactor.cli.rules.ViewHolderRefactoringRule.ShouldNotRefactor;


import R.id.text;
import R.layout.your_layout;




public abstract class Input extends BaseAdapter {
    public static class Adapter1 extends ViewHolderSample {
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return null;
        }
    }
    public static class Adapter5 extends ViewHolderSample {
        LayoutInflater mInflater;

        static class ViewHolderItem {
            TextView text;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = convertView == null ? mInflater.inflate(R.layout.your_layout, null) : convertView;

            ViewHolderItem viewHolderItem = (ViewHolderItem) convertView.getTag();
            if(viewHolderItem == null) {
                convertView.setTag(new ViewHolderItem());
            }
            viewHolderItem.text = viewHolderItem.text != null ? viewHolderItem.text : (TextView) convertView.findViewById(R.id.text);
            viewHolderItem.text.setText("Position " + position);

            return convertView;
        }
    }
}