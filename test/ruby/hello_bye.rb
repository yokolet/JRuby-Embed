# hello_bye.rb [jruby-embed]
# ScriptingContainerTest

class GreetingsImple
  def hello(to)
    print "<p>What's up? ", to, "</p>\n"
    puts "<p>Good? #{to}</p>"
  end
  def bye(to)
    print "See you, ", to, "\n"
  end
end
g = GreetingsImple.new
g.hello("Spring")
g.bye("Winter")
