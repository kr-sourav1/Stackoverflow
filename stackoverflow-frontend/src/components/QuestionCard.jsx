import React from 'react'
import { Link } from 'react-router-dom'
import Tag from './Tag'


export default function QuestionCard({ q }){
  const createdDate = q.createdAt ? new Date(q.createdAt).toLocaleDateString('en-US', { year: 'numeric', month: 'short', day: 'numeric' }) : 'unknown';

  // Debug: log the question object to see what we're receiving (remove in production)
  console.log('QuestionCard received:', q)

  // Resolve content from a few possible fields returned by different APIs
  const rawContent = q?.content ?? q?.body ?? q?.description ?? q?.contentText ?? ''
  const contentStr = typeof rawContent === 'string' ? rawContent : (rawContent ? String(rawContent) : '')
  const contentTrimmed = contentStr.trim()

  return (
    <div className="border border-gray-200 rounded-md p-4 hover:shadow-md transition-shadow bg-white">
      <div className="flex gap-4">
        
        {/* Left: Vote/Answer Stats */}
        <div className="flex flex-col gap-2 w-20 flex-shrink-0">
          <div className="text-center">
            <div className="text-sm text-gray-500">votes</div>
            <div className="text-xl font-semibold text-gray-900">{q.votes ?? 0}</div>
          </div>
          <div className="text-center">
            <div className="text-sm text-gray-500">answers</div>
            <div className="text-xl font-semibold text-gray-900">{(q.answers || []).length}</div>
          </div>
        </div>

        {/* Center: Title, Excerpt, Tags */}
        <div className="flex-1">
          {/* Title */}
          <Link 
            to={`/question/${q.id}`} 
            className="text-lg font-semibold text-blue-600 hover:text-blue-800 line-clamp-2 mb-2"
          >
            {q.title}
          </Link>

          {/* Content Excerpt - show first 300 chars of available content */}
          <p className="text-sm text-gray-700 line-clamp-2 mb-3">
            {contentTrimmed ? contentTrimmed.slice(0, 300) : 'No content available'}
          </p>

          {/* Tags */}
          <div className="flex flex-wrap gap-2">
            {(q.tags || []).map(t => {
              const tagName = typeof t === 'string' ? t : (t.name || String(t))
              const key = typeof t === 'string' ? t : (t.id ?? tagName)
              return <Tag key={key} name={tagName} />
            })}
          </div>
        </div>

        {/* Right: User & Date Info */}
        <div className="flex flex-col items-end gap-2 text-xs text-gray-600 flex-shrink-0">
          <div className="text-right">
            <div className="font-medium text-gray-900">{q.userName || 'anonymous'}</div>
            <div className="text-gray-500">{createdDate}</div>
          </div>
        </div>
      </div>
    </div>
  )
}