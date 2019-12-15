package test.resources.com.leafactor.cli.rules.ViewHolderRefactoringRule.ShouldRefactorConvertView;


import R.layout.your_layout;




public abstract class Input extends BaseAdapter {

    public static class Adapter2 extends ViewHolderSample {
        LayoutInflater mInflater;

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = (convertView == null) ? mInflater.inflate(your_layout, null) : convertView;
            return convertView;
        }
    }
}