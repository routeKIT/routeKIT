package edu.kit.pse.ws2013.routekit.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.kit.pse.ws2013.routekit.models.ProfileMapCombination;
import edu.kit.pse.ws2013.routekit.profiles.Profile;
import edu.kit.pse.ws2013.routekit.views.MainView;
import edu.kit.pse.ws2013.routekit.views.ProfileManagerView;

/**
 * The controller for the {@link ProfileManagerView}.
 */
public class ProfileManagerController {
	private final ProfileManagerView pmv;
	private final Map<String, Profile> profiles; // note: contents of the map
													// are not final at all
	private Profile currentProfile;

	public ProfileManagerController(final MainView view) {
		profiles = new HashMap<>();
		for (final Profile p : ProfileManager.getInstance().getProfiles()) {
			profiles.put(p.getName(), p);
		}
		assert (!profiles.isEmpty());
		// Profile p =
		// MainController.getInstance().getCurrentProfileMap().getProfile();
		Profile p = profiles.values().iterator().next(); // TODO
		assert (p != null);
		currentProfile = p;
		pmv = new ProfileManagerView(view, this, p, new ArrayList<Profile>(
				profiles.values()));
		pmv.setVisible(true);
	}

	private void setAvailableProfiles() {
		assert (!profiles.isEmpty());
		pmv.setAvailableProfiles(new ArrayList<>(profiles.values()));
	}

	/**
	 * Switches to the temporary profile with the given name. If no temporary
	 * profile with the given name exists, it is created as a
	 * {@link Profile#clone() copy} of the current profile.
	 * <p>
	 * The View is informed about this change via
	 * {@link ProfileManagerView#setCurrentProfile}.
	 * 
	 * @param name
	 *            The name of the new profile.
	 */
	public void changeTemporaryProfile(final String name) {
		Profile p = profiles.get(name);
		if (p == null) {
			p = currentProfile.clone();
			p.setName(name);
			profiles.put(name, p);
			setAvailableProfiles();
		}
		setCurrentProfile(p);
	}

	private void setCurrentProfile(Profile p) {
		assert (p != null);
		currentProfile = p;
		pmv.setCurrentProfile(p);
	}

	/**
	 * Marks the currently selected profile for deletion and removes it from the
	 * selection list.
	 * <p>
	 * Profile Note that the profile is only actually deleted in
	 * {@link ProfileManagerController#saveAllChanges}.
	 * <p>
	 * If the currently selected profile is a {@link Profile#isDefault() default
	 * profile}, an {@code IllegalStateException} is thrown.
	 */
	public void deleteCurrentTemporaryProfile() {
		if (currentProfile.isDefault()) {
			throw new IllegalStateException("Can’t delete a default profile!");
		}
		profiles.remove(currentProfile.getName());
		setAvailableProfiles();
		changeTemporaryProfile(profiles.values().iterator().next().getName());
		// TODO use the last selected profile instead of an arbitrary one
	}

	/**
	 * Saves the values of the temporary profile. Usually called before
	 * {@link ProfileManagerController#changeTemporaryProfile}.
	 * 
	 * @param profile
	 *            The temporary profile with the currently entered values.
	 */
	public void saveTemporaryProfile(final Profile profile) {
		Profile p = profiles.get(profile.getName());
		if (p != null && p.isDefault()) {
			if (p.equals(profile)) {
				return;
			}
			throw new IllegalArgumentException(
					"Can’t update a default profile!");
		}
		profiles.put(profile.getName(), profile);
	}

	/**
	 * Executes all requested changes – the adding, changing and deleting of
	 * profiles. For changed profiles, all precalculations are deleted.
	 * 
	 * @see ProfileManager#saveProfile(Profile)
	 * @see ProfileManager#deleteProfile(Profile)
	 */
	public void saveAllChanges() {
		final ProfilesDiff diff = diff();
		final ProfileManager manager = ProfileManager.getInstance();
		for (final Profile deleted : diff.getDeletedProfiles()) {
			manager.deleteProfile(deleted);
		}
		for (final Profile changed : diff.getChangedProfiles()) {
			try {
				manager.saveProfile(changed);
			} catch (IOException e) {
				// TODO throw to the view instead, which could then show an info
				// box
				e.printStackTrace();
			}
		}
		MainController.getInstance().selectProfile(currentProfile);
	}

	/**
	 * Determines how much time the precalculations that will be deleted in
	 * {@link #saveAllChanges()} took, in milliseconds.
	 * 
	 * @return The deletion time, in milliseconds.
	 */
	public int getDeletionTime() {
		final ProfilesDiff diff = diff();
		final Set<Profile> changedProfiles = diff.getChangedProfiles();
		final Set<Profile> deletedProfiles = diff.getDeletedProfiles();
		int time = 0;
		for (final ProfileMapCombination pmc :
		// TODO somewhere.getPrecalculations()
		new HashSet<ProfileMapCombination>()) {
			final Profile p = pmc.getProfile();
			if (changedProfiles.contains(p) || deletedProfiles.contains(p)) {
				time += pmc.getCalculationTime();
			}
		}
		return time;
	}

	private ProfilesDiff diff() {
		return ProfilesDiff.calc(ProfileManager.getInstance().getProfiles(),
				new HashSet<>(profiles.values()));
	}

	private static class ProfilesDiff {
		/**
		 * {@link Profile Profiles} that need to be
		 * {@link ProfileManager#deleteProfile(Profile) deleted}.
		 */
		private final Set<Profile> deletedProfiles;
		/**
		 * {@link Profile Profiles} that need to be
		 * {@link ProfileManager#saveProfile(Profile) saved}, i. e. changed and
		 * new profiles.
		 */
		private final Set<Profile> changedProfiles;

		private ProfilesDiff(final Set<Profile> deletedProfiles,
				final Set<Profile> changedProfiles) {
			this.deletedProfiles = deletedProfiles;
			this.changedProfiles = changedProfiles;
		}

		public Set<Profile> getDeletedProfiles() {
			return deletedProfiles;
		}

		public Set<Profile> getChangedProfiles() {
			return changedProfiles;
		}

		public static ProfilesDiff calc(final Set<Profile> from,
				final Set<Profile> to) {
			final Set<Profile> deletedProfiles = new HashSet<>();
			final Set<Profile> changedProfiles = new HashSet<>();
			for (final Profile p : from) {
				final String pName = p.getName();
				boolean deleted = true;
				for (final Profile q : to) {
					if (pName.equals(q.getName())) {
						deleted = false;
						break;
					}
				}
				if (deleted) {
					deletedProfiles.add(p);
				}
			}
			for (final Profile q : to) {
				final String qName = q.getName();
				boolean changed = true;
				for (final Profile p : from) {
					if (qName.equals(p.getName())) {
						changed = !q.equals(p);
						break;
					}
				}
				if (changed) {
					changedProfiles.add(q);
				}
			}
			return new ProfilesDiff(deletedProfiles, changedProfiles);
		}
	}
}
