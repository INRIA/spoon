/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon;

/**
 * "It was from the artists and poets that the pertinent answers came"
 *
 * In Spoon, we do believe that poetry has a place in software.
 *
 * This class counters the dark side of the force which won at https://github.com/rust-lang/rust/issues/13871
 */
public class LovecraftException extends SpoonException {
	private static final long serialVersionUID = 1L;

	public static final String lovecraft =
		"It was from the artists and poets that the pertinent answers came, and I\n"
		+ "know that panic would have broken loose had they been able to compare notes.\n"
		+ "As it was, lacking their original letters, I half suspected the compiler of\n"
		+ "having asked leading questions, or of having edited the correspondence in\n"
		+ "corroboration of what he had latently resolved to see."
		+ "There are not many persons who know what wonders are opened to them in the\n"
		+ "stories and visions of their youth; for when as children we listen and dream,\n"
		+ "we think but half-formed thoughts, and when as men we try to remember, we are\n"
		+ "dulled and prosaic with the poison of life. But some of us awake in the night\n"
		+ "with strange phantasms of enchanted hills and gardens, of fountains that sing\n"
		+ "in the sun, of golden cliffs overhanging murmuring seas, of plains that stretch\n"
		+ "down to sleeping cities of bronze and stone, and of shadowy companies of heroes\n"
		+ "that ride caparisoned white horses along the edges of thick forests; and then\n"
		+ "we know that we have looked back through the ivory gates into that world of\n"
		+ "wonder which was ours before we were wise and unhappy."
		+ "Instead of the poems I had hoped for, there came only a shuddering blackness\n"
		+ "and ineffable loneliness; and I saw at last a fearful truth which no one had\n"
		+ "ever dared to breathe before — the unwhisperable secret of secrets — The fact\n"
		+ "that this city of stone and stridor is not a sentient perpetuation of Old New\n"
		+ "York as London is of Old London and Paris of Old Paris, but that it is in fact\n"
		+ "quite dead, its sprawling body imperfectly embalmed and infested with queer\n"
		+ "animate things which have nothing to do with it as it was in life."
		+ "The ocean ate the last of the land and poured into the smoking gulf, thereby\n"
		+ "giving up all it had ever conquered. From the new-flooded lands it flowed\n"
		+ "again, uncovering death and decay; and from its ancient and immemorial bed it\n"
		+ "trickled loathsomely, uncovering nighted secrets of the years when Time was\n"
		+ "young and the gods unborn. Above the waves rose weedy remembered spires. The\n"
		+ "moon laid pale lilies of light on dead London, and Paris stood up from its damp\n"
		+ "grave to be sanctified with star-dust. Then rose spires and monoliths that were\n"
		+ "weedy but not remembered; terrible spires and monoliths of lands that men never\n"
		+ "knew were lands..."
		+ "There was a night when winds from unknown spaces whirled us irresistibly into\n"
		+ "limitless vacuum beyond all thought and entity. Perceptions of the most\n"
		+ "maddeningly untransmissible sort thronged upon us; perceptions of infinity\n"
		+ "which at the time convulsed us with joy, yet which are now partly lost to my\n"
		+ "memory and partly incapable of presentation to others."
		+ "You've met with a terrible fate, haven't you?";

	public LovecraftException(String message) {
		super(lovecraft + "\n" + message);
	}
}
