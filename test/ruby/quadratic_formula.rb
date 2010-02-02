# quadratic_formula.rb [jruby-embed]
# GetInstanceTest, RunScriptletTest

# if ax^2+bx+c=0 and b^2-4ac >=0 then
# x = (-b +/- sqrt(b^2-4ac))/2a

def solve(a, b, c)
  v = b ** 2 - 4 * a * c
  if v < 0: raise RangeError end
  s0 = ((-1)*b - Math.sqrt(v))/(2*a)
  s1 = ((-1)*b + Math.sqrt(v))/(2*a)
  return s0, s1
end

begin
  solve(1, -2, 1).each {|s| puts s} # x^2 - 2x + 1 = (x - 1)^2
  solve(1, 2, -8).each {|s| puts s}
  solve(1, 3, -8).each {|s| puts s}
  solve(1, 3, 8).each {|s|  puts s}
rescue RangeError
  puts "solutions are complex"
end