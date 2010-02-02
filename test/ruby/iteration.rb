# iteration.rb [jruby-embed]
# RunScriptletTest

def repeat(t)
  ret =""
  yell = "Trick or Treat!\n"
  t.times do
    ret << yell
  end
  ret
end
