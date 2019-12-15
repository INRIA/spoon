package test.resources.com.leafactor.cli.rules.ViewHolderRefactoringRule.ShouldNotRefactorConvertView;

import R.layout.your_layout;

public abstract class Input extends BaseAdapter {
    public static class Adapter1 extends ViewHolderSample {
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return null;
        }
    }

    public static class Adapter2 extends ViewHolderSample {
        LayoutInflater mInflater;

        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = mInflater.inflate(R.layout.your_layout, null);
            }
            return convertView;
        }
    }

    public static class Adapter3 extends ViewHolderSample {
        LayoutInflater mInflater;

        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView != null) {
                return convertView;
            }
            return  mInflater.inflate(your_layout, null);
        }
    }

//    public static class Adapter4 extends ViewHolderSample {
//        LayoutInflater mInflater;
//
//        public View getView(int position, View convertView, ViewGroup parent) {
//            if(convertView != null) {
//                return convertView;
//            }
//            convertView = mInflater.inflate(your_layout, null);
//            return convertView;
//        }
//    }

    public static class Adapter5 extends ViewHolderSample {
        LayoutInflater mInflater;

        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = convertView == null ? mInflater.inflate(your_layout, null) : convertView;
            return convertView;
        }
    }

    public static class Adapter6 extends ViewHolderSample {
        LayoutInflater inflater;

        @Override
        public View getView(final int position, final View convertView, final ViewGroup parent) {
            View rootView = convertView;
            final int itemViewType = getItemViewType(position);
            switch (itemViewType) {
                case 0:
                    if (rootView != null)
                        return rootView;
                    rootView = inflater.inflate(simple_list_item_1, parent, false);
                    break;
            }
            return rootView;
        }
    }

}