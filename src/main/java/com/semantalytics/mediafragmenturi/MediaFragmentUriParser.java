package com.semantalytics.mediafragmenturi;

import org.parboiled.BaseParser;
import org.parboiled.Rule;
import org.parboiled.annotations.BuildParseTree;

@BuildParseTree
public class MediaFragmentUriParser extends BaseParser<Object> {

    Rule TimeUnit() {
        return FirstOf("s", "%73" );
    }

    Rule TimeFormat() {
        return Sequence(
                 FirstOf( "s", "%73" ),
                 FirstOf( "m", "%6d", "%6D" ),
                 FirstOf( "p", "%70" ),
                 FirstOf( "t", "%74" ),
                 FirstOf( "e", "%65" ),
                 Optional(
                         Sequence( "-",
                             FirstOf(
                                     Sequence(
                                             FirstOf( "2", "%32" ),
                                             FirstOf( "5", "%35" )
                                     ),
                                     Sequence(
                                             FirstOf( "3", "%33" ),
                                             FirstOf( "0", "%30" ),
                                             Optional(
                                                     Sequence( "-",
                                                             FirstOf( "d", "%64" ),
                                                             FirstOf( "r", "%72" ),
                                                             FirstOf( "o", "%6f", "%6F" ),
                                                             FirstOf( "p", "%70" )
                                                     )
                                             )
                                     )
                             )
                         )
                 )
        );
    }

    Rule XywhPrefix() {
        return Sequence(
                FirstOf( "x", "%78" ),
                FirstOf( "y", "%79" ),
                FirstOf( "w", "%77" ),
                FirstOf( "h", "%68" ));
    }

    Rule XywhUnit() {
            return
                    FirstOf(
                            Sequence(
                                    FirstOf( "p", "%70" ),
                                    FirstOf( "i", "%69" ),
                                    FirstOf( "x", "%78" ),
                                    FirstOf( "e", "%65" ),
                                    FirstOf( "l", "%6c", "%6C" )
                            ),
                            Sequence(
                                    FirstOf( "p", "%70"),
                                    FirstOf( "e", "%65"),
                                    FirstOf( "r", "%72"),
                                    FirstOf( "c",  "%63"),
                                    FirstOf( "e", "%65"),
                                    FirstOf( "n", "%6e", "%6E"),
                                    FirstOf( "t", "%74")
                            )
                    );
    }

    Rule TrackPrefix() {
        return Sequence(
                FirstOf( "t", "%74" ),
                FirstOf( "r", "%72" ),
                FirstOf( "a", "%61" ),
                FirstOf( "c", "%63" ),
                FirstOf( "k", "%6b", "%6B" )
        );
    }

    Rule Colon() {
        return Ch(':');
    }

    Rule Eq() {
        return Ch('=');
    }

    Rule Amp() {
        return Ch('&');
    }

    Rule Comma() {
        return Ch(',');
    }

    Rule Dot() {
        return FirstOf(".", "%2E", "%2e");
    }

    Rule Alpha() {
        return Sequence(
                CharRange('A', 'Z'),
                CharRange('a', 'z'));
    }

    Rule Digit() {
        return CharRange('0', '9');
    }

    Rule HexDig() {
        return FirstOf(
                Digit(),
                FirstOf(
                        CharRange('a', 'f'),
                        CharRange('A', 'F')
                )
        );
    }

    Rule PDigit() {
        return Sequence("%3", Digit());
    }

    Rule Unreserved() {
        return FirstOf(Alpha(), Digit(), "-", ".", "_", "~");
    }

    Rule PCTEncoded() {
        return Sequence("%", HexDig(), HexDig());
    }

    Rule SubDelims() {
        return FirstOf("!", "$", "&", "'", "(", ")", "*", "+", ",", ";", "=");
    }

    Rule Utf8String() {
        return OneOrMore(
                Unreserved(),
                PCTEncoded(),
                TimePrefix(),
                TimeUnit(),
                DefTimeFormat(),
                XywhPrefix(),
                XywhUnit(),
                TrackPrefix(),
                NamePrefix(),
                Digit(),
                PDigit()
        );
    }

    Rule ParserUnit() {
        return Sequence(
                MediaSegment(),
                EOI
        );
    }

    Rule MediaSegment() {
        return FirstOf(
                NameSegment(),
                AxisSegment()
        );
    }

    Rule NameSegment() {
        return Sequence(
                NamePrefix(),
                Eq(),
                NameParam()
        );
    }

    Rule NamePrefix() {
        return Sequence(
                FirstOf( "i", "%69" ),
                FirstOf( "d", "%64" )
        );
    }

    Rule NameParam() {
        return Utf8String();
    }

    Rule AxisSegment() {
        return FirstOf(
                Sequence(TimeSegment(), "&", TestNot(OneOrMore(TimeSegment()))),
                Sequence(SpaceSegment(), "&", TestNot(OneOrMore(SpaceSegment()))),
                Sequence(TrackSegment(), "&", TestNot(OneOrMore(TrackSegment()))));
    }

    Rule TimeSegment() {
        return Sequence(
                TimePrefix(),
                Eq(),
                TimeParam()
        );
    }

    Rule TimePrefix() {
        return FirstOf( "t", "%74" );
    }

    Rule TimeParam() {
        return FirstOf(
                NptTimeDef(),
                OtherTimeDef()
        );
    }

    Rule NptTimeDef() {
       return Sequence(
               Optional(
                       DefTimeFormat(),
                       Colon()
               ),
               FirstOf(
                       Sequence(
                               ClockTime(),
                               Optional(
                                       Sequence(
                                               Comma(),
                                               ClockTime()
                                       )
                               )
                       ),
                       Sequence(
                               Comma(),
                               ClockTime()
                       )
               )
       );
    }

    Rule DefTimeFormat () {
        return Sequence(
                FirstOf( "n", "%6e", "%6E" ),
                FirstOf( "p", "%70" ),
                FirstOf( "t", "%74" ));
    }

    Rule ClockTime() {
        return  Sequence(
                OneOrMore(
                        PDigit()
                ),
                FirstOf(
                        Sequence(
                                Dot(),
                                ZeroOrMore(
                                        PDigit()
                                ),
                                Optional(
                                        TimeUnit()
                                )
                        ),
                        Sequence(
                                Colon(),
                                PDigit(),
                                PDigit(),
                                Optional(
                                        Sequence(
                                                Colon(),
                                                PDigit(),
                                                PDigit()
                                        )
                                ),
                                Optional(
                                        Dot(),
                                        ZeroOrMore(
                                                PDigit()
                                        )
                                )
                        )
                )
        );
    }

    Rule OtherTimeDef() {
        return Sequence(TimeFormat(), Colon(), FirstOf(FrameTime(), Optional(Comma(), FrameTime())), Comma(), FrameTime());
    }

    Rule FrameTime() {
        return Sequence(OneOrMore(PDigit()), Colon(), PDigit(), PDigit(), Colon(), PDigit(), PDigit(), Optional(Colon(), PDigit(), PDigit(), Optional(Dot(), PDigit(), PDigit())));
    }

    Rule SpaceSegment() {
        return XywhDef();
    }

    Rule XywhDef() {
        return Sequence(XywhParam(), Eq(), XywhParam());
    }

    Rule XywhParam() {
        return Sequence(Optional(XywhUnit(), Colon()), OneOrMore(PDigit()), Comma(), OneOrMore(PDigit()), Comma(), OneOrMore(PDigit()), Comma(), OneOrMore(PDigit()));
    }

    Rule TrackSegment() {
        return Sequence(
                TrackPrefix(),
                Eq(),
                TrackParam()
        );
    }

    Rule TrackParam() {
        return Utf8String();
    }
}
