(function ($) {
    $.fn.listnav = function (options) {
        var opts = $.extend({}, $.fn.listnav.defaults, options),
            letters = ['_', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '-'],
            firstClick = false,
            clickEventType=((document.ontouchstart!==null)?'click':'touchstart');
        opts.prefixes = $.map(opts.prefixes, function (n) {
            return n.toLowerCase();
        });
        return this.each(function () {
            var $wrapper, $letters, $letterCount,
                id = this.id,
                $list = $(this),
                counts = {},
                allCount = 0,
                isAll = true,
                prevLetter = '';
            if ( !$('#' + id + '-nav').length ) {
                $('<div id="' + id + '-nav" class="listNav"/>').insertBefore($list);
            }
            $wrapper = $('#' + id + '-nav');
            function init() {
                $wrapper.append(createLettersHtml());
                $letters = $('.ln-letters', $wrapper).slice(0, 1);
                addClasses();
                addNoMatchLI();
                bindHandlers();
                if (opts.flagDisabled) {
                    addDisabledClass();
                }
                if ( !opts.includeAll ) {
                    $('.all', $letters).remove();
                }
                if ( !opts.includeNums ) {
                    $('._', $letters).remove();
                }
                if ( !opts.includeOther ) {
                    $('.-', $letters).remove();
                }
                if ( opts.removeDisabled ) {
                    $('.ln-disabled', $letters).remove();
                }
                $(':last', $letters).addClass('ln-last');
                if ( $.cookie && (opts.cookieName !== null) ) {
                    var cookieLetter = $.cookie(opts.cookieName);
                    if ( cookieLetter !== null ) {
                        opts.initLetter = cookieLetter;
                    }
                }
                if ( opts.initLetter !== '' ) {
                    firstClick = true;
                    $('.' + opts.initLetter.toLowerCase(), $letters).slice(0, 1).trigger(clickEventType);
                } else {
                    if ( opts.includeAll ) {
                        $('.all', $letters).addClass('ln-selected');
                    } else {
                        for ( var i = ((opts.includeNums) ? 0 : 1); i < letters.length; i++) {
                            if ( counts[letters[i]] > 0 ) {
                                firstClick = true;
                                $('.' + letters[i], $letters).slice(0, 1).trigger(clickEventType);
                                break;
                            }
                        }
                    }
                }
            }
            function addClasses() {
                var str, spl, $this,
                    firstChar = '',
                    hasPrefixes = (opts.prefixes.length > 0),
                    hasFilterSelector = (opts.filterSelector.length > 0);
                $($list).children().each(function () {
                    $this = $(this);
                    if ( !hasFilterSelector ) {
                        str = $.trim($this.text()).toLowerCase();
                    } else {
                        str = $.trim($this.find(opts.filterSelector).text()).toLowerCase();
                    }
                    if (str !== '') {
                        if (hasPrefixes) {
                            var prefixes = $.map(opts.prefixes, function(value) {
                                return value.indexOf(' ') <= 0 ? value + ' ' : value;
                            });
                            var matches = $.grep(prefixes, function(value) {
                                return str.indexOf(value) === 0;
                            });
                            if (matches.length > 0) {
                                var afterMatch = str.toLowerCase().split(matches[0])[1];
                                if(afterMatch != null) {
                                    firstChar = $.trim(afterMatch).charAt(0);
                                } else {
                                    firstChar = str.charAt(0);
                                }
                                addLetterClass(firstChar, $this, true);
                                return;
                            }
                        }
                        firstChar = str.charAt(0);
                        addLetterClass(firstChar, $this);
                    }
                });
            }
            function addLetterClass(firstChar, $el, isPrefix) {
                if ( /\W/.test(firstChar) ) {
                    firstChar = '-';
                }
                if ( !isNaN(firstChar) ) {
                    firstChar = '_';
                }
                $el.addClass('ln-' + firstChar);
                if ( counts[firstChar] === undefined ) {
                    counts[firstChar] = 0;
                }
                counts[firstChar]++;
                if (!isPrefix) {
                    allCount++;
                }
            }
            function addDisabledClass() {
                for ( var i = 0; i < letters.length; i++ ) {
                    if ( counts[letters[i]] === undefined ) {
                        $('.' + letters[i], $letters).addClass('ln-disabled');
                    }
                }
            }
            function addNoMatchLI() {
                $list.append('<li class="ln-no-match listNavHide">' + opts.noMatchText + '</li>');
            }
            function getLetterCount(el) {
                if ($(el).hasClass('all')) {
                    return allCount;
                } else {
                    var count = counts[$(el).attr('class').split(' ')[0]];
                    return (count !== undefined) ? count : 0; // some letters may not have a count in the hash
                }
            }
            function bindHandlers() {
                if (opts.showCounts) {
                    $wrapper.mouseover(function () {
                        setLetterCountTop();
                    });
                    $('.ln-letters a', $wrapper).mouseover(function () {
                        var left = $(this).position().left,
                            width = ($(this).outerWidth()) + 'px',
                            count = getLetterCount(this);
                        $letterCount.css({
                            left: left,
                            width: width
                        }).text(count).addClass("letterCountShow").removeClass("listNavHide");
                    }).mouseout(function () {
                        $letterCount.addClass("listNavHide").removeClass("letterCountShow");
                    });
                }
                $('a', $letters).bind(clickEventType, function (e) {
                    e.preventDefault();
                    var $this = $(this),
                        letter = $this.attr('class').split(' ')[0],
                        noMatches = $list.children('.ln-no-match');
                    if ( prevLetter !== letter ) {
                        $('a.ln-selected', $letters).removeClass('ln-selected');
                        if ( letter === 'all' ) {
                            $list.children().addClass("listNavShow").removeClass("listNavHide");
                            noMatches.addClass("listNavHide").removeClass("listNavShow");
                            isAll = true;
                        } else {
                            if ( isAll ) {
                                $list.children().addClass("listNavHide").removeClass("listNavShow");
                                isAll = false;
                            } else if (prevLetter !== '') {
                                $list.children('.ln-' + prevLetter).addClass("listNavHide").removeClass("listNavShow");
                            }
                            var count = getLetterCount(this);
                            if (count > 0) {
                                $list.children('.ln-' + letter).addClass("listNavShow").removeClass("listNavHide");
                                noMatches.addClass("listNavHide").removeClass("listNavShow"); // in case it's showing
                            } else {
                                noMatches.addClass("listNavShow").removeClass("listNavHide");
                            }
                        }
                        prevLetter = letter;
                        if ($.cookie && (opts.cookieName !== null)) {
                            $.cookie(opts.cookieName, letter, {
                                expires: 999
                            });
                        }
                        $this.addClass('ln-selected');
                        $this.blur();
                        if (!firstClick && (opts.onClick !== null)) {
                            opts.onClick(letter);
                        } else {
                            firstClick = false;
                        }
                    }
                });
            }
            function createLettersHtml() {
                var html = [];
                for (var i = 1; i < letters.length; i++) {
                    if (html.length === 0) {
                        html.push('<p>汽车用品品牌大全</p><a class="all" href="#">ALL</a>');
                    }
                    html.push('<a class="' + letters[i] + '" href="javascript:;">' + ((letters[i] === '-') ? '...' : letters[i].toUpperCase()) + '</a>');
                }
                return '<div class="ln-letters">' + html.join('') + '</div>' + ((opts.showCounts) ? '<div class="ln-letter-count listNavHide">0</div>' : '');
            }
            init();
        });
    };

    $.fn.listnav.defaults = {
        initLetter: '',
        includeAll: true,
        incudeOther: false,
        includeNums: true,
        flagDisabled: true,
        removeDisabled: false,
        noMatchText: '<p>暂无品牌图片</p>',
        showCounts: true,
        cookieName: null,
        onClick: null,
        prefixes: [],
        filterSelector: ''
    };
})(jQuery);
