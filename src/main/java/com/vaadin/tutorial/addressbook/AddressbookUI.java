package com.vaadin.tutorial.addressbook;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.SelectionEvent;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.tutorial.addressbook.backend.Contact;
import com.vaadin.tutorial.addressbook.backend.ContactService;
import com.vaadin.ui.*;

import javax.servlet.annotation.WebServlet;

/* Web user interface in Java.
 * Define the user interface by extending the UI class. A new instance of
 * this class is automatically created for every user accessing the application.
 * This can also be a managed bean (CDI or Spring).
 */

// Normally UI is (re-)created when URL is loaded.
// Use @PreserveOnRefresh here to automatically preserve
// the full state also over browser reloads.
@Title("Addressbook")
@Theme("valo")
public class AddressbookUI extends UI {


	/* Hundreds of widgets.
	 * Vaadin's user interface components are just Java objects that encapsulate
	 * and handle cross-browser support and client-server communication. The
	 * default Vaadin components are in the com.vaadin.ui package and there
	 * are over 500 more in vaadin.com/directory.
	 */
	private TextField filter = new TextField();
	private Button newContact = new Button("New contact");
	private Grid contactList = new Grid();

	// ContactForm is an example of a custom component class
	private ContactForm contactForm = new ContactForm(this);

	// ContactService is a in-memory mock DAO that mimics
	// a real-world datasource. Typically implemented for
	// example as EJB or Spring Data based service.
	private ContactService service = ContactService.createDemoService();


	/* The "main method".
	 * This is the entry point method executed to initialize and configure
	 * the visible user interface. Executed on every browser reload.
	 */
	@Override
	protected void init(VaadinRequest request) {

		// If you need to configure the components, the init
		// method is a good place to do that.
		filter.setInputPrompt("Filter contacts...");
		contactList.setSelectionMode(Grid.SelectionMode.SINGLE);


		/* Easy event-driven programming.
		 * Synchronously receive user interaction events on the server-side.
		 * Vaadin automatically tracks component changes and sends them back
		 * to the browser.
		 */
		// Attach listeners for click event, selection and filter text change
		newContact.addClickListener((Button.ClickEvent e)
				-> editContact(new Contact()));
		filter.addTextChangeListener((TextChangeEvent e)
				-> listContacts(e.getText()));
		contactList.addSelectionListener((SelectionEvent e)
				-> editContact((Contact) contactList.getSelectedRow()));


		/* Building the layout.
		 * Layouts are components that contain other components.
		 * HorizontalLayout contains TextField and Button. It is wrapped with a Grid
		 * into VerticalLayout on the left side of the screen. Allow users to resize
		 * the components with a SplitPanel. In addition to Java, you may also choose
		 *
		 * In addition to Java, you may also choose Vaadin Designer,
		 * CSS and HTML templates or declarative format for
		 * creating your layouts.
		 */
		HorizontalLayout actions = new HorizontalLayout(filter, newContact);
		actions.setWidth("100%");
		filter.setWidth("100%");
		actions.setExpandRatio(filter, 1);

		VerticalLayout left = new VerticalLayout(actions, contactList);
		left.setSizeFull();
		contactList.setSizeFull();
		left.setExpandRatio(contactList, 1);

		// Split and allow resizing
		setContent(new HorizontalSplitPanel(left, contactForm));

		// Setup grid columns
		contactList.setContainerDataSource(new BeanItemContainer<>(Contact.class));
		contactList.setColumnOrder("firstName", "lastName", "email");
		contactList.removeColumn("id");
		contactList.removeColumn("birthDate");
		contactList.removeColumn("phone");

		// List initial content from the back-end data source
		refreshContacts();
	}

	/* Use design patterns.
	 * It is good practice to have separate data access methods that
	 * handle the back-end access and/or the user interface updates.
	 * With Vaadin you can follow MVC, MVP or any design pattern you prefer.
	 *
	 */
	private void listContacts(String stringFilter) {
		contactList.setContainerDataSource(new BeanItemContainer<>(
				Contact.class, service.findAll(stringFilter)));
		contactForm.setVisible(false);
	}

	private void editContact(Contact contact) {
		if (contact != null) {
			// let the ContactForm decide how contact is edited
			contactForm.edit(contact);
		} else {
			/* Server-side code security.
			 * Components hidden in server-side code do not
			 * accept the input/updates from browser.
			 */
			// Hide the form from user
			contactForm.setVisible(false);
		}
	}


	/*
	 * The refreshContacts() and deselect() methods are called by custom ContactForm when user wants to
	 * persist or reset changes to the edited contact.
	 */
	public void refreshContacts() {
		listContacts(filter.getValue());
	}

	public void deselect() {
		contactList.select(null);
	}

	public ContactService getService() {
		return this.service;
	}

	/*  Deploy as a Servlet or Portlet.
	 *
	 *  You can specify additional servlet parameters like the URI and UI
	 *  class name and turn on production mode when you have finished developing the application.
	 *
	 */
	@WebServlet(urlPatterns = "/*")
	@VaadinServletConfiguration(ui = AddressbookUI.class, productionMode = false)
	public static class MyUIServlet extends VaadinServlet {
	}


}
