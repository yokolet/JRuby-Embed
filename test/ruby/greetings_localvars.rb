# greetings_localvars.rb [jruby-embed]
# ScriptingContainerTest

def greet
  message = "How're you doing? #{who}."
end

def sayhi
  $, = ","
  $\ = "\n"
  print "Hello,", people
end

def count
  print "I talked #{people.size + 1} people in total."
end