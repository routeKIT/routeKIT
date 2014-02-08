package edu.kit.pse.ws2013.routekit.controllers;

import java.awt.Window;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.kit.pse.ws2013.routekit.models.ProfileMapCombination;
import edu.kit.pse.ws2013.routekit.models.ProgressReporter;
import edu.kit.pse.ws2013.routekit.profiles.Profile;
import edu.kit.pse.ws2013.routekit.views.ProfileManagerView;

/**
 * The controller for the {@link ProfileManagerView}.
 */
public class ProfileManagerController {
	private final ProfileManagerView pmv;
	private final Map<String, Profile> profiles; // note: contents of the map
													// are not final at all
	private final Set<ProfileMapCombination> combinationsAtStartup;
	private Profile currentProfile;
	private Profile selectedProfile; // set in saveAllChanges

	public ProfileManagerController(final Window parent) {
		combinationsAtStartup = new HashSet<>(ProfileMapManager.getInstance()
				.getPrecalculations());
		profiles = new HashMap<>();
		for (final Profile p : ProfileManager.getInstance().getProfiles()) {
			profiles.put(p.getName(), p);
		}
		assert (!profiles.isEmpty());
		Profile p = ProfileMapManager.getInstance().getCurrentCombination()
				.getProfile();
		assert (p != null);
		currentProfile = p;
		pmv = new ProfileManagerView(parent, this, p, new HashSet<>(
				profiles.values()));
		pmv.setVisible(true);
	}

	private void setAvailableProfiles() {
		assert (!profiles.isEmpty());
		pmv.setAvailableProfiles(new HashSet<>(profiles.values()));
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
	 * @throws IllegalArgumentException
	 *             If the profile name is
	 *             {@link ProfileManager#checkProfileName(String) invalid}.
	 */
	public void changeTemporaryProfile(final String name) {
		Profile p = profiles.get(name);
		if (p == null) {
			if (!ProfileManager.getInstance().checkProfileName(name)) {
				throw new IllegalArgumentException("Invalid profile name!");
			}
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
	 * @throws IllegalArgumentException
	 *             If the profile is a {@link Profile#isDefault()} profile.
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
		if (profile.getName().equals(currentProfile.getName())) {
			currentProfile = profile;
		}
	}

	/**
	 * Executes all requested changes – the adding, changing and deleting of
	 * profiles. For changed profiles, all precalculations are deleted.
	 * <p>
	 * The given {@link ProgressReporter} should already have the task
	 * "Saving changes" or something similar pushed onto it. This method will
	 * then push and pop sub-tasks.
	 * <p>
	 * The changes are executed asynchronously in a new worker thread, and after
	 * all changes have been executed, an additional task is popped off the
	 * reporter that this method did not push (the task "Saving changes",
	 * mentioned earlier). This way, the caller may be notified when the changes
	 * are done.
	 * 
	 * @param reporter
	 *            The {@link ProgressReporter} to report progress to.
	 * @see ProfileManager#saveProfile(Profile)
	 * @see ProfileManager#deleteProfile(Profile)
	 */
	public void saveAllChanges(final ProgressReporter reporter) {
		new Thread() {
			@Override
			public void run() {
				selectedProfile = currentProfile;
				reporter.setSubTasks(new float[] { .1f, .4f, .5f });
				reporter.pushTask("Ermittle Änderungen");
				final ProfilesDiff diff = diff();
				reporter.popTask();
				final ProfileManager manager = ProfileManager.getInstance();
				reporter.pushTask("Lösche Profile");
				reporter.setSubTasks(diff.getDeletedProfiles().size());
				for (final Profile deleted : diff.getDeletedProfiles()) {
					reporter.pushTask("Lösche Profil '" + deleted.getName()
							+ "'");
					manager.deleteProfile(deleted);
					reporter.popTask();
				}
				reporter.nextTask("Speichere Profile");
				reporter.setSubTasks(diff.getChangedProfiles().size());
				for (final Profile changed : diff.getChangedProfiles()) {
					try {
						reporter.pushTask("Speichere Profil '"
								+ changed.getName() + "'");
						manager.saveProfile(changed);
						reporter.popTask();
					} catch (IOException e) {
						MainController.getInstance().view.textMessage(e
								.getMessage());
					}
				}
				reporter.popTask();
				reporter.popTask(); // pop root task
			}
		}.start();
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
		final Set<String> changedProfileNames = new HashSet<>();
		for (Profile p : changedProfiles) {
			changedProfileNames.add(p.getName());
		}
		int time = 0;
		for (final ProfileMapCombination pmc : ProfileMapManager.getInstance()
				.getPrecalculations()) {
			final Profile p = pmc.getProfile();
			if (changedProfileNames.contains(p.getName())
					|| deletedProfiles.contains(p)) {
				time += pmc.getCalculationTime();
			}
		}
		return time;
	}

	/**
	 * Returns the profile that the user selected. Note that this is different
	 * from the <i>current</i> profile (which is the one that the user has
	 * currently selected, while the view is still visible); the selected
	 * profile is only set in {@link #saveAllChanges()}, and if that method is
	 * never called (e.&nbsp;g. because the user clicked “Cancel”), then this
	 * method returns {@code null} to indicate that.
	 * 
	 * @return The profile that the user selected.
	 */
	public Profile getSelectedProfile() {
		return selectedProfile;
	}

	/**
	 * Returns the precalculations that were deleted because a profile was
	 * changed or deleted.
	 * 
	 * @return The deleted precalculations.
	 */
	public Set<ProfileMapCombination> getDeletedPrecalculations() {
		final ProfilesDiff diff = diff();
		final Set<Profile> profiles = diff.getDeletedProfiles();
		profiles.addAll(diff.getChangedProfiles());
		final Set<ProfileMapCombination> ret = new HashSet<>();
		for (ProfileMapCombination combination : combinationsAtStartup) {
			if (profiles.contains(combination.getProfile())) {
				ret.add(combination);
			}
		}
		return ret;
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
