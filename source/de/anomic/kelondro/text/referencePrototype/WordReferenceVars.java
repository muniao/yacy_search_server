// ReferenceVars.java
// (C) 2007 by Michael Peter Christen; mc@yacy.net, Frankfurt a. M., Germany
// first published 07.11.2007 on http://yacy.net
//
// This is a part of YaCy, a peer-to-peer based web search engine
//
// $LastChangedDate: 2009-03-20 16:44:59 +0100 (Fr, 20 Mrz 2009) $
// $LastChangedRevision: 5736 $
// $LastChangedBy: borg-0300 $
//
// LICENSE
// 
// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

package de.anomic.kelondro.text.referencePrototype;

import java.util.ArrayList;

import de.anomic.kelondro.index.Row.Entry;
import de.anomic.kelondro.order.Bitfield;
import de.anomic.kelondro.order.MicroDate;
import de.anomic.kelondro.text.AbstractReference;
import de.anomic.kelondro.text.Reference;

public class WordReferenceVars  extends AbstractReference implements WordReference, Reference, Cloneable {

    public Bitfield flags;
    public long lastModified;
    public String language, urlHash;
    public char type;
    public int hitcount, llocal, lother, phrasesintext,
               posinphrase, posofphrase,
               urlcomps, urllength, virtualAge,
               wordsintext, wordsintitle;
    ArrayList<Integer> positions;
    public double termFrequency;
    
    public WordReferenceVars(final String  urlHash,
            final int      urlLength,     // byte-length of complete URL
            final int      urlComps,      // number of path components
            final int      titleLength,   // length of description/length (longer are better?)
            final int      hitcount,      // how often appears this word in the text
            final int      wordcount,     // total number of words
            final int      phrasecount,   // total number of phrases
            final ArrayList<Integer> ps,  // positions of words that are joined into the reference
            final int      posinphrase,   // position of word in its phrase
            final int      posofphrase,   // number of the phrase where word appears
            final long     lastmodified,  // last-modified time of the document where word appears
            final long     updatetime,    // update time; this is needed to compute a TTL for the word, so it can be removed easily if the TTL is short
                  String   language,      // (guessed) language of document
            final char     doctype,       // type of document
            final int      outlinksSame,  // outlinks to same domain
            final int      outlinksOther, // outlinks to other domain
            final Bitfield flags,  // attributes to the url and to the word according the url
            final double   termfrequency
    ) {
        if ((language == null) || (language.length() != 2)) language = "uk";
        final int mddlm = MicroDate.microDateDays(lastmodified);
        //final int mddct = MicroDate.microDateDays(updatetime);
        this.flags = flags;
        //this.freshUntil = Math.max(0, mddlm + (mddct - mddlm) * 2);
        this.lastModified = lastmodified;
        this.language = language;
        this.urlHash = urlHash;
        this.type = doctype;
        this.hitcount = hitcount;
        this.llocal = outlinksSame;
        this.lother = outlinksOther;
        this.phrasesintext = outlinksOther;
        this.positions = new ArrayList<Integer>(ps.size());
        for (int i = 0; i < ps.size(); i++) this.positions.add(ps.get(i));
        this.posinphrase = posinphrase;
        this.posofphrase = posofphrase;
        this.urlcomps = urlComps;
        this.urllength = urlLength;
        this.virtualAge = mddlm;
        this.wordsintext = wordcount;
        this.wordsintitle = titleLength;
        this.termFrequency = termfrequency;
    }
    
    public WordReferenceVars(final WordReference e) {
        this.flags = e.flags();
        //this.freshUntil = e.freshUntil();
        this.lastModified = e.lastModified();
        this.language = e.getLanguage();
        this.urlHash = e.metadataHash();
        this.type = e.getType();
        this.hitcount = e.hitcount();
        this.llocal = e.llocal();
        this.lother = e.lother();
        this.phrasesintext = e.phrasesintext();
        this.positions = new ArrayList<Integer>(e.positions());
        for (int i = 0; i < e.positions(); i++) this.positions.add(e.position(i));
        this.posinphrase = e.posinphrase();
        this.posofphrase = e.posofphrase();
        this.urlcomps = e.urlcomps();
        this.urllength = e.urllength();
        this.virtualAge = e.virtualAge();
        this.wordsintext = e.wordsintext();
        this.wordsintitle = e.wordsintitle();
        this.termFrequency = e.termFrequency();
    }
    
    public WordReferenceVars clone() {
        final WordReferenceVars c = new WordReferenceVars(
                this.urlHash,
                this.urllength,
                this.urlcomps,
                this.wordsintitle,
                this.hitcount,
                this.wordsintext,
                this.phrasesintext,
                this.positions,
                this.posinphrase,
                this.posofphrase,
                this.lastModified,
                System.currentTimeMillis(),
                this.language,
                this.type,
                this.llocal,
                this.lother,
                this.flags,
                this.termFrequency);
        return c;
    }
    
    public void join(final WordReferenceVars v) {
        // combine the distance
        this.positions.addAll(v.positions);
        this.posinphrase = (this.posofphrase == v.posofphrase) ? Math.min(this.posinphrase, v.posinphrase) : 0;
        this.posofphrase = Math.min(this.posofphrase, v.posofphrase);

        // combine term frequency
        this.wordsintext = this.wordsintext + v.wordsintext;
        this.termFrequency = this.termFrequency + v.termFrequency;
    }

    public Bitfield flags() {
        return flags;
    }
/*
    public long freshUntil() {
        return freshUntil;
    }
*/
    public String getLanguage() {
        return language;
    }

    public char getType() {
        return type;
    }

    public int hitcount() {
        return hitcount;
    }

    public boolean isOlder(final Reference other) {
        assert false; // should not be used
        return false;
    }

    public long lastModified() {
        return lastModified;
    }

    public int llocal() {
        return llocal;
    }

    public int lother() {
        return lother;
    }

    public int phrasesintext() {
        return phrasesintext;
    }

    public int posinphrase() {
        return posinphrase;
    }

    public int positions() {
        return this.positions.size();
    }

    public int position(int p) {
        return this.positions.get(p);
    }

    public int posofphrase() {
        return posofphrase;
    }
    
    public WordReferenceRow toRowEntry() {
        return new WordReferenceRow(
                urlHash,
                urllength,     // byte-length of complete URL
                urlcomps,      // number of path components
                wordsintitle,  // length of description/length (longer are better?)
                hitcount,      // how often appears this word in the text
                wordsintext,   // total number of words
                phrasesintext, // total number of phrases
                positions.get(0), // position of word in all words
                posinphrase,   // position of word in its phrase
                posofphrase,   // number of the phrase where word appears
                lastModified,  // last-modified time of the document where word appears
                System.currentTimeMillis(),    // update time;
                language,      // (guessed) language of document
                type,          // type of document
                llocal,        // outlinks to same domain
                lother,        // outlinks to other domain
                flags          // attributes to the url and to the word according the url
        );
    }
    
    public Entry toKelondroEntry() {
        return toRowEntry().toKelondroEntry();
    }

    public String toPropertyForm() {
        return toRowEntry().toPropertyForm();
    }

    public String metadataHash() {
        return urlHash;
    }

    public int urlcomps() {
        return urlcomps;
    }

    public int urllength() {
        return urllength;
    }

    public int virtualAge() {
        return virtualAge;
    }

    public int wordsintext() {
        return wordsintext;
    }

    public int wordsintitle() {
        return wordsintitle;
    }

    public double termFrequency() {
        if (this.termFrequency == 0.0) this.termFrequency = (((double) this.hitcount()) / ((double) (this.wordsintext() + this.wordsintitle() + 1)));
        return this.termFrequency;
    }
    
    public final void min(final WordReferenceVars other) {
        int v;
        long w;
        double d;
        if (this.hitcount > (v = other.hitcount)) this.hitcount = v;
        if (this.llocal > (v = other.llocal)) this.llocal = v;
        if (this.lother > (v = other.lother)) this.lother = v;
        if (this.virtualAge > (v = other.virtualAge)) this.virtualAge = v;
        if (this.wordsintext > (v = other.wordsintext)) this.wordsintext = v;
        if (this.phrasesintext > (v = other.phrasesintext)) this.phrasesintext = v;
        this.positions = a(Math.min(min(this.positions), min(other.positions)));
        if (this.posinphrase > (v = other.posinphrase)) this.posinphrase = v;
        if (this.posofphrase > (v = other.posofphrase)) this.posofphrase = v;
        if (this.lastModified > (w = other.lastModified)) this.lastModified = w;
        //if (this.freshUntil > (w = other.freshUntil)) this.freshUntil = w;
        if (this.urllength > (v = other.urllength)) this.urllength = v;
        if (this.urlcomps > (v = other.urlcomps)) this.urlcomps = v;
        if (this.wordsintitle > (v = other.wordsintitle)) this.wordsintitle = v;
        if (this.termFrequency > (d = other.termFrequency)) this.termFrequency = d;
    }
    
    public final void max(final WordReferenceVars other) {
        int v;
        long w;
        double d;
        if (this.hitcount < (v = other.hitcount)) this.hitcount = v;
        if (this.llocal < (v = other.llocal)) this.llocal = v;
        if (this.lother < (v = other.lother)) this.lother = v;
        if (this.virtualAge < (v = other.virtualAge)) this.virtualAge = v;
        if (this.wordsintext < (v = other.wordsintext)) this.wordsintext = v;
        if (this.phrasesintext < (v = other.phrasesintext)) this.phrasesintext = v;
        this.positions = a(Math.max(max(this.positions), max(other.positions)));
        if (this.posinphrase < (v = other.posinphrase)) this.posinphrase = v;
        if (this.posofphrase < (v = other.posofphrase)) this.posofphrase = v;
        if (this.lastModified < (w = other.lastModified)) this.lastModified = w;
        //if (this.freshUntil < (w = other.freshUntil)) this.freshUntil = w;
        if (this.urllength < (v = other.urllength)) this.urllength = v;
        if (this.urlcomps < (v = other.urlcomps)) this.urlcomps = v;
        if (this.wordsintitle < (v = other.wordsintitle)) this.wordsintitle = v;
        if (this.termFrequency < (d = other.termFrequency)) this.termFrequency = d;
    }

    public void join(final Reference r) {
        // joins two entries into one entry
        
        // combine the distance
        WordReference oe = (WordReference) r; 
        for (int i = 0; i < r.positions(); i++) this.positions.add(r.position(i));
        this.posinphrase = (this.posofphrase == oe.posofphrase()) ? Math.min(this.posinphrase, oe.posinphrase()) : 0;
        this.posofphrase = Math.min(this.posofphrase, oe.posofphrase());

        // combine term frequency
        this.termFrequency = this.termFrequency + oe.termFrequency();
        this.wordsintext = this.wordsintext + oe.wordsintext();
    }

    public int hashCode() {
        return this.urlHash.hashCode();
    }

    public void addPosition(int position) {
        this.positions.add(position);
    }

    
}
