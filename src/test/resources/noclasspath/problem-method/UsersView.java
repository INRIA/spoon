package de.unibremen.st.gradelog.view;

import com.vaadin.cdi.CDIView;
import com.vaadin.data.Binder;
import com.vaadin.data.HasValue;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.event.selection.SelectionEvent;
import com.vaadin.event.selection.SelectionListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.shared.ui.colorpicker.Color;
import com.vaadin.ui.*;
import com.vaadin.ui.components.grid.EditorSaveEvent;
import com.vaadin.ui.components.grid.EditorSaveListener;
import com.vaadin.ui.components.grid.ItemClickListener;
import de.unibremen.st.gradelog.exception.DuplicateUniqueFieldException;
import de.unibremen.st.gradelog.model.User;
import de.unibremen.st.gradelog.persistence.UserDAO;

import javax.inject.Inject;
import java.time.LocalDateTime;

@CDIView
public class UsersView extends CustomComponent implements View {

    @Inject
    private UserDAO userDao;

    private Grid<User> table;

    @Override
    public void enter(final ViewChangeListener.ViewChangeEvent event) {
        final UsersEditController controller = new UsersEditController();

        table = new Grid<>();
        table.setSizeFull();
        table.getEditor().setEnabled(true);
        table.setDataProvider(DataProvider.ofCollection(userDao.getAllUsers()));

        table.addColumn(User::getUsername).setId("username")
                .setEditorBinding(createUserBinding("test")).setCaption("Username");
        table.addColumn(User::getEmail).setCaption("Email");
        table.addColumn(User::getLanguage).setCaption("Language");
        // table.appendHeaderRow().getCell("username").setComponent(new TextField());

        table.getEditor().addSaveListener(controller);

        final Layout layout = new VerticalLayout();
        layout.addComponent(table);
        setCompositionRoot(layout);
    }

    private Binder.Binding<User, String> createUserBinding() {
        return table
                .getEditor()
                .getBinder()
                .forField(new TextField())
                .withValidator(s -> s.length() >= 3,
                        "Username must consists of at least three characters")
                .bind(User::getUsername, User::setUsername);
    }

    private Binder.Binding<User, String> createUserBinding(final String string) {
        return table
                .getEditor()
                .getBinder()
                .forField(new TextField())
                .withValidator(s -> s.length() >= 3,
                        "Username must consists of at least three characters")
                .bind(User::getUsername, User::setUsername);
    }

    private class UsersEditController implements EditorSaveListener<User>,
            SelectionListener<User> {

        private User selectedUser;

        @Override
        public void onEditorSave(final EditorSaveEvent<User> theEvent) {
            try {
                userDao.update(selectedUser);
            } catch (final DuplicateUniqueFieldException e) {
                selectedUser.setUsername(userDao.getById(selectedUser.getId())
                        .getUsername());
                Notification.show(e.getMessage());
            }
        }

        @Override
        public void selectionChange(final SelectionEvent<User> event) {
            selectedUser = event.getFirstSelectedItem().orElse(selectedUser);
        }
    }
}
