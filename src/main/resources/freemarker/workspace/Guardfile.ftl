# Run: bundler exec guard -p -l 5 -w ${"<directory>"}

guard :shell do
    log = Logger.new(STDOUT)

    extensions = [
    	'class', 'java', 'groovy', 'kt', 'scala', 'sc', 'clj',
    	'jsp', 'vm', 'ftl', 'json', 'xml', 'yml', 'yaml', 'properties',
    	'html', 'css', 'js', 'vue', 'ts', 'txt', 'png'
    ]

    excludes = [ 'node_modules/', '.git/', '.idea/' ]

    r_watches = Regexp.new("\\.${"#"}{Regexp.union(extensions)}\\z")
    r_excludes = Regexp.union(excludes)

    watch(r_watches) { |m|
    	if r_excludes !~ m[0]
            log.info("${"#"}{m[0]} has been changed.")

            atime = File.atime(m[0])
            mtime =  File.mtime(m[0])

  			File.utime(atime, mtime, m[0])
		end
    }
end